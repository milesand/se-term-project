package seproj.shrimpsnack.addon.sim;

import seproj.shrimpsnack.sim.SimImpl;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class InstanceSIMConnection implements SIMConnection {
	
	private SimImpl sim;

	public InstanceSIMConnection(SimImpl sim) {
		this.sim = sim;
	}

	@Override
	public boolean detectHazard() {
		return this.sim.detectHazard();
	}

	@Override
	public boolean[] detectBlob() {
		return this.sim.detectBlobs();
	}

	@Override
	public Pair getPosition() {
		int x = this.sim.x();
		int y = this.sim.y();
		return new Pair(x, y);
	}

	@Override
	public Direction getDirection() {
		seproj.shrimpsnack.sim.Direction d = this.sim.direction();
		switch (d) {
		case N:
			return Direction.N;
		case E:
			return Direction.E;
		case S:
			return Direction.S;
		default:
			return Direction.W;
		}
	}

	@Override
	public Pair getSize() {
		int w = this.sim.map_width();
		int h = this.sim.map_height();
		return new Pair(w, h);
	}

	@Override
	public void moveForward() {
		this.sim.moveForward();
	}

	@Override
	public void turn() throws Exception {
		this.sim.turnClockwise();
	}

}
