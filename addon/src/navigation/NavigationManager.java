package navigation;

import map.MapManager;
import sim.SIMConnection;
import utility.Direction;
import utility.Pair;

public class NavigationManager {

	private SIMConnection sim;
	private MapManager mm;
	private PositionList destinations;
	private PositionList path;
	private Pair prev_pos;
	private String state;

	public Pair[] navigate() {
	}

	public void add_destination(int idx, Pair pos) {
	}

	public Pair remove_destination(int idx) {
	}

	public Pair[] destinations_view() {
	}

	private void move_forward() {
	}

	private void turn_to(Direction dir) {
	}

	private void plan_path() {
	}

}
