package seproj.shrimpsnack.addon.navigation;

import java.util.ArrayList;
import java.util.List;

import seproj.shrimpsnack.addon.map.MapManager;
import seproj.shrimpsnack.addon.map.MapView;
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

	public List<Pair> navigate() throws Exception {

		ArrayList<Pair> history = new ArrayList<Pair>(); //
		Pair current_dst = this.destinations.current();
		if (current_dst == null) { // Destination list was empty, we're done exploring 'all' of them
			return history;
		}

		while (true) {
			Pair current_pos = this.mm.position();
			history.add(current_pos); 
			this.mm.markBlobs(); // detect and mark blobs

			if (current_dst.equals(current_pos)) {
				// Our current path leads to our current destination, which we've reached.
				// Clear our path, to signal to later code that a new path must be planned.
				this.path.clear();

				// Loop, because some madman might have queued the same position
				// multiple times consecutively.
				do {
					this.destinations.advance(); // increase index
					current_dst = this.destinations.current();
					if (current_dst == null) {
						return history;
					}
				} while (current_dst.equals(current_pos));
			}

			// Check our position, and set up this.path so that calling current() on it
			// returns our next, non-null position.

			Pair expected_pos = this.path.current(); // Current expected position.
			if (expected_pos == null) {
				this.planPath();
				if (this.path == null) {
					// We've failed to plan a path because the next destination is unreachable.
					// We were told not to take this case into account, so I'll just... return null
					// here.
					// Note that there's at least one more of this 'plan path failed' down the lane,
					// so if you're changing this to do something else be sure to check there too,
					return null;
				}

				// The first item in our new path is current_pos.
				// We're interested in the next position, so skip it.
				this.path.advance();

				// next_pos cannot be null since
				// 1) We've just planned a new path
				// 2) We've made sure we're not in our current destination in the loop above
				// 3) Thus a new path consists of at least two items, Current position and
				// destination.

			} else if (expected_pos.equals(current_pos)) {

				this.path.advance();

				// We know that we're not in the current destination cell, thus the path should
				// contain at least one more position. Therefore, next_pos cannot be null.

			} else if (this.prev_pos == null || !this.prev_pos.equals(current_pos)) {

				// The robot is not in it's old position, nor in its expected position;
				// that would mean the robot moved two cells forward.
				// Check whether we can reuse the old path by checking our next expected
				// position.
				// If that equals our current position, carry on. Otherwise, plan a new path.

				this.path.advance();
				Pair next_expected = this.path.current();
				if (next_expected == null || !next_expected.equals(current_pos)) {
					// This can't fail. Both current destination and current cell were reachable
					// from the old cell, so we should still be able to reach the destination.
					this.planPath();
				}
				this.path.advance();

				// If we've planned a new path, see the expected_pos == null branch's comment.
				// If we're reusing the old path, see the expected_pos.equals(current_pos)
				// branch.
			}

			// In case the robot didn't move, we don't have to do anything!
			// We carry on without advancing the path.

			while (true) {
				Pair next_pos = this.path.current(); // The cell we'll try to move into.

				Direction step_direction;
				switch (next_pos.y - current_pos.y) {
				case 1:
					step_direction = Direction.N;
					break;
				case -1:
					step_direction = Direction.S;
					break;
				default: // case 0:
					if (next_pos.x - current_pos.x == 1) {
						step_direction = Direction.E;
					} else {
						step_direction = Direction.W;
					}
				}
				this.turnTo(step_direction);
				this.mm.setDirection(step_direction);

				if (!this.mm.checkHazard()) {
					this.prev_pos = current_pos;
					this.mm.invalidatePosition();
					this.moveForward();
					break;
				}

				// There was a hazard in front of us, and we couldn't go forward.
				// Plan a new path.
				this.planPath();

				if (this.path == null) {
					// Another unreachable destination case. Let's be consistent and return null
					// here too.
					return null;
				}
				// Advance to grab the proper next position.
				this.path.advance();
			}
		}
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

	private void planPath() throws Exception {
		Pair current_pos = this.mm.position();
		Pair destination = this.destinations.current();
		MapView map = this.mm.mapView();

		List<Pair> path = PathFindingAlgorithm.run(current_pos, destination, map);
		this.path = new PositionList(path);
	}

}
