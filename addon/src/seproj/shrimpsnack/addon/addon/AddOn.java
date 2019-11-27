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

	public void addDestination(int idx, Pair pos) {
	}

	public Pair removeDestination(int idx) {
	}

	public Pair[] destinationsView() {
	}

	public void addHazard(Pair pos) {
	}

	public void removeHazard(Pair pos) {
	}

	public MapView mapView() {
	}

	public Pair[] navigate() {
	}
}
