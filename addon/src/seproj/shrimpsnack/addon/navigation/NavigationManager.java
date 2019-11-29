package seproj.shrimpsnack.addon.navigation;

import java.util.List;

import seproj.shrimpsnack.addon.map.MapManager;
import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class NavigationManager {

	private SIMConnection sim;
	private MapManager mm;
	private PositionList destinations;
	private PositionList path;
	private Pair prev_pos;
	private String state;

	public NavigationManager(SIMConnection sim, MapManager mm) {
		this.sim = sim;
		this.mm = mm;
		this.destinations = new PositionList();
		this.path = new PositionList();
		this.prev_pos = null;
	}

	public Pair[] navigate() {
	}

	public void addDestination(int idx, Pair pos) {
		this.destinations.addPos(idx, pos);
	}

	public Pair removeDestination(int idx) {
		return this.destinations.removePos(idx);
	}

	public List<Pair> destinationsView() {
		return this.destinations.view();
	}

	private void moveForward() throws Exception {
		this.mm.invalidatePosition();
		this.sim.moveForward();
	}

	private void turnTo(Direction dir) throws Exception {
		Direction current_dir = this.mm.direction();
		while (!current_dir.equals(dir)) {
			this.sim.turn();
			current_dir = current_dir.nextClockwise();
		}
		this.mm.setDirection(current_dir);
	}

	private void planPath() {
	}

}
