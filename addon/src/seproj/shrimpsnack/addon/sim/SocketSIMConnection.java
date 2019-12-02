package seproj.shrimpsnack.addon.sim;

import java.net.Socket;
import java.net.UnknownHostException;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class SocketSIMConnection implements SIMConnection {

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

	final static byte OK = 0;
	final static byte ERR = 1;

	final static Direction[] DIRECTIONS = Direction.values();

	private final Socket conn;
	private final DataInputStream in;
	private final DataOutputStream out;

	private SocketSIMConnection(Builder builder) {
		this.conn = builder.conn;
		this.in = builder.in;
		this.out = builder.out;
	}

	public boolean detectHazard() throws IOException {
		this.out.writeByte(Command.DETECT_HAZARD.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		return this.in.readBoolean();
	}

	public boolean[] detectBlob() throws IOException {
		this.out.writeByte(Command.DETECT_BLOBS.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		boolean[] ret = new boolean[4];
		for (int i = 0; i < 4; i++) {
			ret[i] = this.in.readBoolean();
		}
		return ret;
	}

	public Pair getPosition() throws IOException {
		this.out.writeByte(Command.GET_BOT_POS.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		int x = this.in.readInt();
		int y = this.in.readInt();
		return new Pair(x, y);
	}

	public Direction getDirection() throws IOException {
		this.out.writeByte(Command.GET_BOT_DIR.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		return DIRECTIONS[this.in.readByte()];
	}

	public Pair getSize() throws IOException {
		this.out.writeByte(Command.GET_MAP_SIZE.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		int w = this.in.readInt();
		int h = this.in.readInt();
		return new Pair(w, h);
	}

	public void moveForward() throws IOException {
		this.out.writeByte(Command.MOVE_FORWARD.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
	}

	public void turn() throws IOException {
		this.out.writeByte(Command.TURN_CW.ordinal());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
	}

	public void close() throws IOException {
		this.conn.close();
	}

	public static class Builder {
		private final Socket conn;
		private final DataInputStream in;
		private final DataOutputStream out;

		public Builder(String host, int port) throws UnknownHostException, IOException {
			this.conn = new Socket(host, port);
			this.in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
			this.out = new DataOutputStream(conn.getOutputStream());
		}

		public SocketSIMConnection build() throws IOException {
			this.out.writeByte(Command.BUILD.ordinal());
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return new SocketSIMConnection(this);
		}

		public Builder mapSize(Pair size) throws IOException {
			this.out.writeByte(Command.SET_MAP_SIZE.ordinal());
			this.out.writeInt(size.x);
			this.out.writeInt(size.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder robotPosition(Pair pos) throws IOException {
			this.out.writeByte(Command.SET_BOT_POS.ordinal());
			this.out.writeInt(pos.x);
			this.out.writeInt(pos.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder robotDirection(Direction direction) throws IOException {
			this.out.writeByte(Command.SET_BOT_DIR.ordinal());
			this.out.writeByte(direction.ordinal());
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder addHazard(Pair pos) throws IOException {
			this.out.writeByte(Command.ADD_HAZARD.ordinal());
			this.out.writeInt(pos.x);
			this.out.writeInt(pos.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder removeHazard(Pair pos) throws IOException {
			this.out.writeByte(Command.REMOVE_HAZARD.ordinal());
			this.out.writeInt(pos.x);
			this.out.writeInt(pos.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder addBlob(Pair pos) throws IOException {
			this.out.writeByte(Command.ADD_BLOB.ordinal());
			this.out.writeInt(pos.x);
			this.out.writeInt(pos.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder removeBlob(Pair pos) throws IOException {
			this.out.writeByte(Command.REMOVE_BLOB.ordinal());
			this.out.writeInt(pos.x);
			this.out.writeInt(pos.y);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder setSeed(Long seed) throws IOException {
			this.out.writeByte(Command.SET_SEED.ordinal());
			this.out.writeLong(seed);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder setNoMovementProbability(float prob) throws IOException {
			this.out.writeByte(Command.SET_PROB_0.ordinal());
			this.out.writeFloat(prob);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}

		public Builder setDoubleForwardProbability(float prob) throws IOException {
			this.out.writeByte(Command.SET_PROB_2.ordinal());
			this.out.writeFloat(prob);
			this.out.flush();
			if (this.in.readByte() != OK) {
				throw new SocketSIMConnectionException("SIM returned Error");
			}
			return this;
		}
	}
}
