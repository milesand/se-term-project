package seproj.shrimpsnack.addon.addon;

import java.util.List;

import seproj.shrimpsnack.addon.map.MapManager;
import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.navigation.NavigationManager;
import seproj.shrimpsnack.addon.navigation.NavigationStepResult;
import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Pair;

public class AddOn {
	private NavigationManager nm;
	private MapManager mm;

	public AddOn(SIMConnection sim) throws Exception {
		this.mm = new MapManager(sim);
		this.nm = new NavigationManager(sim, this.mm);
	}

	public void addDestination(int idx, Pair pos) {
		this.nm.addDestination(idx, pos);
	}

	public Pair removeDestination(int idx) {
		return this.nm.removeDestination(idx);
	}

	public List<Pair> destinationsView() {
		return this.nm.destinationsView();
	}

	public void addHazard(Pair pos) {
		this.mm.setHazard(pos);
	}

	public void removeHazard(Pair pos) {
		this.mm.unsetHazard(pos);
	}

	public MapView mapView() {
		return this.mm.mapView();
	}

	public NavigationStepResult navigate() throws Exception {
		return this.nm.navigate();
	}
}
