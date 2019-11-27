package addon;

import map.MapManager;
import map.MapView;
import navigation.NavigationManager;
import sim.SIMConnection;
import utility.Pair;

public class AddOn {
	private NavigationManager nm;
	private MapManager mm;

	public AddOn(SIMConnection sim) {
	}

	public void add_destination(int idx, Pair pos) {
	}

	public Pair remove_destination(int idx) {
	}

	public Pair[] destinations_view() {
	}

	public void add_hazard(Pair pos) {
	}

	public void remove_hazard(Pair pos) {
	}

	public MapView map_view() {
	}

	public Pair[] navigate() {
	}
}
