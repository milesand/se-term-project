package sim;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;

public class SimProcess {

	enum Command {
		// 0
		SET_MAP_SIZE, SET_BOT_POS, SET_BOT_DIR, ADD_HAZARD, REMOVE_HAZARD,
		// 5
		ADD_BLOB, REMOVE_BLOB, SET_SEED, SET_PROB_0, SET_PROB_2,
		// 10
		BUILD, // End of Builder-state commands

		GET_BOT_DIR, GET_MAP_SIZE, GET_BOT_POS, DETECT_HAZARD,
		// 15
		DETECT_BLOBS, MOVE_FORWARD, TURN_CW;
	}

	final static Command[] COMMANDS = Command.values();
	final static Direction[] DIRECTIONS = Direction.values();

	final static byte OK = 0;
	final static byte ERR = 1;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("Error: Missing port argument");
			return;
		}

		int port;
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			System.err.printf("Error: Found invalid port argument: %s\n", args[0]);
			return;
		}

		// We'll deal with just one connection, for simplicity.
		try (ServerSocket listener = new ServerSocket(port); Socket conn = listener.accept();) {

			DataInputStream in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
			DataOutputStream out = new DataOutputStream(new BufferedOutputStream(conn.getOutputStream()));

			SimImpl sim = handle_builder_request(in, out);
			handle_sim_request(in, out, sim);

		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
	}

	static SimImpl handle_builder_request(DataInputStream in, DataOutputStream out) throws IOException {
		// Create a builder, and two sets for storing coordinates of places-of-interest,
		// and RNG seed.
		SimImpl.Builder builder = new SimImpl.Builder();
		HashSet<SimImpl.Coordinates> hazards = new HashSet<SimImpl.Coordinates>();
		HashSet<SimImpl.Coordinates> blobs = new HashSet<SimImpl.Coordinates>();
		Long seed = null;

		while (true) {
			for (byte command = in.readByte(); COMMANDS[command] != Command.BUILD; command = in.readByte()) {
				try {
					if (command >= COMMANDS.length) {
						throw new IllegalArgumentException();
					}
					switch (COMMANDS[command]) {
					case SET_MAP_SIZE:
						int w = in.readInt();
						int h = in.readInt();
						builder.mapSize(w, h);
						break;
					case SET_BOT_POS:
						int x = in.readInt();
						int y = in.readInt();
						builder.robotPosition(x, y);
						break;
					case SET_BOT_DIR:
						byte dir_idx = in.readByte();
						if (dir_idx >= DIRECTIONS.length) {
							throw new IllegalArgumentException();
						}
						builder.robotDirection(DIRECTIONS[dir_idx]);
						break;
					case ADD_HAZARD:
						x = in.readInt();
						y = in.readInt();
						hazards.add(new SimImpl.Coordinates(x, y));
						break;
					case REMOVE_HAZARD:
						x = in.readInt();
						y = in.readInt();
						hazards.remove(new SimImpl.Coordinates(x, y));
						break;
					case ADD_BLOB:
						x = in.readInt();
						y = in.readInt();
						blobs.add(new SimImpl.Coordinates(x, y));
						break;
					case REMOVE_BLOB:
						x = in.readInt();
						y = in.readInt();
						blobs.remove(new SimImpl.Coordinates(x, y));
						break;
					case SET_SEED:
						seed = in.readLong();
						break;
					case SET_PROB_0:
						float prob = in.readFloat();
						builder.setNoMovementProbability(prob);
						break;
					case SET_PROB_2:
						prob = in.readFloat();
						builder.setDoubleForwardProbability(prob);
						break;
					default:
						throw new IllegalArgumentException();
					}
				} catch (IllegalArgumentException e) {
					out.writeByte(ERR);
					out.flush();
					continue;
				}
				out.writeByte(OK);
				out.flush();
			}

			// If we reached here, command equaled Command.BUILD and thus we should try
			// building SimImpl.
			try {
				Random rng;
				if (seed == null) {
					rng = new Random();
				} else {
					rng = new Random(seed);
				}
				SimImpl sim = builder.hazards(hazards.stream()).blobs(blobs.stream()).rng(rng).build();
				// Success: tell the user we've built it and return to caller.
				out.writeByte(OK);
				out.flush();
				return sim;

			} catch (IllegalStateException | IndexOutOfBoundsException | IllegalArgumentException e) {
				// We just signal error to user and keep looping.
				out.writeByte(ERR);
				out.flush();
			}
		}
	}

	static void handle_sim_request(DataInputStream in, DataOutputStream out, SimImpl sim) throws IOException {
		while (true) {
			byte command = in.readByte();
			if (command >= COMMANDS.length) {
				out.writeByte(ERR);
				out.flush();
				continue;
			}
			switch (COMMANDS[command]) {

			case GET_BOT_DIR:
				Direction dir = sim.direction();
				byte dir_encoded = (byte) dir.ordinal();
				out.writeByte(OK);
				out.writeByte(dir_encoded);
				out.flush();
				break;

			case GET_MAP_SIZE:
				int w = sim.map_width();
				int h = sim.map_height();
				out.writeByte(OK);
				out.writeInt(w);
				out.writeInt(h);
				out.flush();
				break;

			case GET_BOT_POS:
				int x = sim.x();
				int y = sim.y();
				out.writeByte(OK);
				out.writeInt(x);
				out.writeInt(y);
				out.flush();
				break;

			case DETECT_HAZARD:
				boolean hazard = sim.detectHazard();
				out.writeByte(OK);
				out.writeBoolean(hazard);
				out.flush();
				break;

			case DETECT_BLOBS:
				boolean[] blobs = sim.detectBlobs();
				out.writeByte(OK);
				for (boolean blob : blobs) {
					out.writeBoolean(blob);
				}
				out.flush();
				break;

			case MOVE_FORWARD:
				if (sim.moveForward()) {
					out.writeByte(OK);
				} else {
					out.writeByte(ERR);
				}
				out.flush();
				break;

			case TURN_CW:
				sim.turnClockwise();
				out.writeByte(OK);
				out.flush();
				break;

			default:
				out.writeByte(ERR);
				out.flush();
				break;
			}
		}
	}
}
