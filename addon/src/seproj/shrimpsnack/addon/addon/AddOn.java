package seproj.shrimpsnack.addon.addon;

import seproj.shrimpsnack.addon.map.MapManager;
import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.navigation.NavigationManager;
import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Pair;

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
