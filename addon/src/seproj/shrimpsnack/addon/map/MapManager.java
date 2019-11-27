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

	public boolean check_hazard() {
	}

	public void set_hazard(Pair pos) {
	}

	public void unset_hazard(Pair pos) {
	}

	public MapView map_view() {
	}

	public void invalidate_position() {
	}

	public Pair position() {
		return this.pos;
	}

	public Direction direction() {
		return this.dir;
	}

	public void set_direction(Direction dir) {
		this.dir = dir;
	}
}
