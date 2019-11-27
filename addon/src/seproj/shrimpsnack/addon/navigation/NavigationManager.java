package seproj.shrimpsnack.addon.navigation;

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

	public Pair[] navigate() {
	}

	public void addDestination(int idx, Pair pos) {
	}

	public Pair removeDestination(int idx) {
	}

	public Pair[] destinationsView() {
	}

	private void moveForward() {
	}

	private void turnTo(Direction dir) {
	}

	private void planPath() {
	}

}
