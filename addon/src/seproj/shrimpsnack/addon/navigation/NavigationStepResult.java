package seproj.shrimpsnack.addon.navigation;

import java.util.List;

import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class NavigationStepResult {
	public final Pair robot_pos;
	public final Direction robot_dir;
	public final List<Pair> remaining_path;
	public final List<Pair> destinations_reached;
	public final List<Pair> remaining_destinations;
	public final MapView map_view;

	public NavigationStepResult(Pair robot_pos, Direction robot_dir, List<Pair> remaining_path,
			List<Pair> destinations_reached, List<Pair> remaining_destinations, MapView map_view) {
		this.robot_pos = robot_pos;
		this.robot_dir = robot_dir;
		this.remaining_path = remaining_path;
		this.destinations_reached = destinations_reached;
		this.remaining_destinations = remaining_destinations;
		this.map_view = map_view;
	}
}
