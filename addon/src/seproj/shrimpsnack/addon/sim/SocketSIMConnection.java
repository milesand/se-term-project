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
		// 11
		GET_BOT_DIR, GET_MAP_SIZE, GET_BOT_POS, DETECT_HAZARD,
		// 15
		DETECT_BLOBS, MOVE_FORWARD, TURN_CW;

		byte to_byte() {
			return (byte) (this.ordinal() + 11);
		}
	}

	final static byte OK = 0;
	final static byte ERR = 1;

	final static Direction[] DIRECTIONS = Direction.values();

	private final Socket conn;
	private final DataInputStream in;
	private final DataOutputStream out;

	public SocketSIMConnection(String host, int port) throws UnknownHostException, IOException {
		this.conn = new Socket(host, port);
		this.in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
		this.out = new DataOutputStream(conn.getOutputStream());
	}

	public boolean detectHazard() throws IOException {
		this.out.writeByte(Command.DETECT_HAZARD.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		return this.in.readBoolean();
	}

	public boolean[] detectBlob() throws IOException {
		this.out.writeByte(Command.DETECT_BLOBS.to_byte());
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
		this.out.writeByte(Command.DETECT_BLOBS.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		int x = this.in.readInt();
		int y = this.in.readInt();
		return new Pair(x, y);
	}

	public Direction getDirection() throws IOException {
		this.out.writeByte(Command.GET_BOT_DIR.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		return DIRECTIONS[this.in.readByte()];
	}

	public Pair getSize() throws IOException {
		this.out.writeByte(Command.GET_MAP_SIZE.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
		int w = this.in.readInt();
		int h = this.in.readInt();
		return new Pair(w, h);
	}

	public void moveForward() throws IOException {
		this.out.writeByte(Command.MOVE_FORWARD.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
	}

	public void turn() throws IOException {
		this.out.writeByte(Command.TURN_CW.to_byte());
		this.out.flush();
		if (this.in.readByte() != OK) {
			throw new SocketSIMConnectionException("SIM returned Error");
		}
	}

	public void close() throws IOException {
		this.conn.close();
	}
}
