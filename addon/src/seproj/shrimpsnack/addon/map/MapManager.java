package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class MapManager {
	private SIMConnection sim;
	private Map map;
	private Pair pos;
	private Direction dir;

	public void mark_blobs() {
	}

	public boolean checkHazard() {
	}

	public void setHazard(Pair pos) {
	}

	public void unsetHazard(Pair pos) {
	}

	public MapView mapView() {
	}

	public void invalidatePosition() {
	}

	public Pair position() {
		return this.pos;
	}

	public Direction direction() {
		return this.dir;
	}

	public void setDirection(Direction dir) {
		this.dir = dir;
	}
}
