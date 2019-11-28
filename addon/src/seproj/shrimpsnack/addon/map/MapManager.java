package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.OptionalBool;
import seproj.shrimpsnack.addon.utility.Pair;

public class MapManager {
	private SIMConnection sim;
	private Map map;
	private Pair pos;
	private Direction dir;

	public void markBlobs() throws Exception {
		Pair pos = this.position();

		Cell[] unknown_adj_cells = new Cell[4];
		boolean found_unknown = false;

		Direction dir = Direction.N;
		for (int i = 0; i < 4; i++) {
			try {
				Pair adj_pos = new Pair(pos.x + dir.x(), pos.y + dir.y());
				Cell adj_cell = this.map.get(adj_pos);
				if (adj_cell.isBlob().equals(OptionalBool.Unknown)) {
					unknown_adj_cells[i] = adj_cell;
					found_unknown = true;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
			} finally {
				dir = dir.nextClockwise();
			}
		}

		if (!found_unknown) {
			return;
		}

		boolean[] blobs = this.sim.detectBlob();
		for (int i = 0; i < 4; i++) {
			if (unknown_adj_cells[i] == null) {
				continue;
			}
			unknown_adj_cells[i].setBlob(blobs[i]);
		}
	}

	public boolean checkHazard() throws Exception {
		Pair pos = this.position();
		Direction dir = this.direction();
		Pair forward_pos = new Pair(pos.x + dir.x(), pos.y + dir.y());
		Cell forward_cell = this.map.get(forward_pos);

		boolean hazard = false;
		switch (forward_cell.isHazard()) {
		case Unknown:
			hazard = this.sim.detectHazard();
			forward_cell.setHazard(hazard);
			break;

		case True:
			hazard = true;
			break;

		case False:
			hazard = false;
			break;
		}

		return hazard;
	}

	public void setHazard(Pair pos) {
		this.map.get(pos).setHazard(true);
	}

	public void unsetHazard(Pair pos) {
		this.map.get(pos).setHazard(false);
	}

	public MapView mapView() {
		return new MapView(this.map);
	}

	public void invalidatePosition() {
		this.pos = null;
	}

	public Pair position() throws Exception {
		if (this.pos == null) {
			this.pos = this.sim.getPosition();
		}
		return this.pos;
	}

	public Direction direction() {
		return this.dir;
	}

	public void setDirection(Direction dir) {
		this.dir = dir;
	}
}
