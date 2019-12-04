package seproj.shrimpsnack.sim;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;

public class SimProcess {

	static enum Command {
		// 0
		SET_MAP_SIZE, SET_BOT_POS, SET_BOT_DIR, ADD_HAZARD, REMOVE_HAZARD,
		// 5
		ADD_BLOB, REMOVE_BLOB, SET_SEED, SET_PROB_0, SET_PROB_2,
		// 10
		BUILD, // End of Builder-state commands

		GET_BOT_DIR, GET_MAP_SIZE, GET_BOT_POS, DETECT_HAZARD,
		// 15
		DETECT_BLOBS, MOVE_FORWARD, TURN_CW,

		CLOSE;
	}

	final static Command[] COMMANDS = Command.values();
	final static Direction[] DIRECTIONS = Direction.values();

	final static byte OK = 0;
	final static byte ERR = 1;

	final DataInputStream in;
	final DataOutputStream out;

	SimProcess(InputStream in, OutputStream out) {
		this.in = new DataInputStream(new BufferedInputStream(in));
		this.out = new DataOutputStream(new BufferedOutputStream(out));
	}

	private void run() throws IOException {
		SimImpl sim = this.handle_builder_request();
		if (sim == null) {
			return;
		}
		this.handle_sim_request(sim);
	}

	public static void main(String[] args) throws IOException {
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

		try (ServerSocket listener = new ServerSocket(port)) {
			while (true) {
				Socket conn = listener.accept();
				Thread t = new Thread(() -> {
					try {
						new SimProcess(conn.getInputStream(), conn.getOutputStream()).run();
					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						conn.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				});
				t.run();
			}
		}
	}

	private SimImpl handle_builder_request() throws IOException {
		// Create a builder, and two sets for storing coordinates of places-of-interest,
		// and RNG seed.
		SimImpl.Builder builder = new SimImpl.Builder();
		HashSet<SimImpl.Coordinates> hazards = new HashSet<SimImpl.Coordinates>();
		HashSet<SimImpl.Coordinates> blobs = new HashSet<SimImpl.Coordinates>();
		Long seed = null;

		byte command;
		while (true) {
			while (true) {
				try {
					command = this.in.readByte();
				} catch (EOFException e) {
					return null;
				}
				try {
					if (command >= COMMANDS.length) {
						throw new IllegalArgumentException();
					}
					if (COMMANDS[command].equals(Command.BUILD)) {
						break;
					}
					switch (COMMANDS[command]) {
					case SET_MAP_SIZE:
						int w = this.in.readInt();
						int h = this.in.readInt();
						builder.mapSize(w, h);
						break;
					case SET_BOT_POS:
						int x = this.in.readInt();
						int y = this.in.readInt();
						builder.robotPosition(x, y);
						break;
					case SET_BOT_DIR:
						byte dir_idx = this.in.readByte();
						if (dir_idx >= DIRECTIONS.length) {
							throw new IllegalArgumentException();
						}
						builder.robotDirection(DIRECTIONS[dir_idx]);
						break;
					case ADD_HAZARD:
						x = this.in.readInt();
						y = this.in.readInt();
						hazards.add(new SimImpl.Coordinates(x, y));
						break;
					case REMOVE_HAZARD:
						x = this.in.readInt();
						y = this.in.readInt();
						hazards.remove(new SimImpl.Coordinates(x, y));
						break;
					case ADD_BLOB:
						x = this.in.readInt();
						y = this.in.readInt();
						blobs.add(new SimImpl.Coordinates(x, y));
						break;
					case REMOVE_BLOB:
						x = this.in.readInt();
						y = this.in.readInt();
						blobs.remove(new SimImpl.Coordinates(x, y));
						break;
					case SET_SEED:
						seed = this.in.readLong();
						break;
					case SET_PROB_0:
						float prob = this.in.readFloat();
						builder.setNoMovementProbability(prob);
						break;
					case SET_PROB_2:
						prob = this.in.readFloat();
						builder.setDoubleForwardProbability(prob);
						break;
					case CLOSE:
						this.out.writeByte(OK);
						this.out.flush();
						return null;
					default:
						throw new IllegalArgumentException();
					}
				} catch (IllegalArgumentException e) {
					this.out.writeByte(ERR);
					this.out.flush();
					continue;
				}
				this.out.writeByte(OK);
				this.out.flush();
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
				this.out.writeByte(OK);
				this.out.flush();
				return sim;

			} catch (IllegalStateException | IndexOutOfBoundsException | IllegalArgumentException e) {
				// We just signal error to user and keep looping.
				this.out.writeByte(ERR);
				this.out.flush();
			}
		}
	}

	private void handle_sim_request(SimImpl sim) throws IOException {
		byte command;
		while (true) {
			try {
				command = this.in.readByte();
			} catch (EOFException e) {
				return;
			}

			if (command >= COMMANDS.length) {
				this.out.writeByte(ERR);
				this.out.flush();
				continue;
			}

			switch (COMMANDS[command]) {

			case GET_BOT_DIR:
				Direction dir = sim.direction();
				byte dir_encoded = (byte) dir.ordinal();
				this.out.writeByte(OK);
				this.out.writeByte(dir_encoded);
				this.out.flush();
				break;

			case GET_MAP_SIZE:
				int w = sim.map_width();
				int h = sim.map_height();
				this.out.writeByte(OK);
				this.out.writeInt(w);
				this.out.writeInt(h);
				this.out.flush();
				break;

			case GET_BOT_POS:
				int x = sim.x();
				int y = sim.y();
				this.out.writeByte(OK);
				this.out.writeInt(x);
				this.out.writeInt(y);
				this.out.flush();
				break;

			case DETECT_HAZARD:
				boolean hazard = sim.detectHazard();
				this.out.writeByte(OK);
				this.out.writeBoolean(hazard);
				this.out.flush();
				break;

			case DETECT_BLOBS:
				boolean[] blobs = sim.detectBlobs();
				this.out.writeByte(OK);
				for (boolean blob : blobs) {
					this.out.writeBoolean(blob);
				}
				this.out.flush();
				break;

			case MOVE_FORWARD:
				if (sim.moveForward()) {
					this.out.writeByte(OK);
				} else {
					this.out.writeByte(ERR);
				}
				this.out.flush();
				break;

			case TURN_CW:
				sim.turnClockwise();
				this.out.writeByte(OK);
				this.out.flush();
				break;
			
			case CLOSE:
				this.out.writeByte(OK);
				this.out.flush();
				return;

			default:
				this.out.writeByte(ERR);
				this.out.flush();
				break;
			}
		}
	}
}
