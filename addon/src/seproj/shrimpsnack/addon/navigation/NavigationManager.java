package seproj.shrimpsnack.addon.navigation;

import java.util.ArrayList;
import java.util.List;

import seproj.shrimpsnack.addon.map.MapManager;
import seproj.shrimpsnack.addon.map.MapView;
import seproj.shrimpsnack.addon.sim.SIMConnection;
import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public class NavigationManager {
	
	private enum State {
		NOT_NAVIGATING,
		MOVED,
		DETECTED_BLOB,
		TURNED,
		DETECTED_HAZARD,
	}

	private SIMConnection sim;
	private MapManager mm;
	private PositionList destinations;
	private PositionList path;
	private Pair prev_pos;
	private State state;

	public NavigationManager(SIMConnection sim, MapManager mm) {
		this.sim = sim;
		this.mm = mm;
		this.destinations = new PositionList();
		this.path = new PositionList();
		this.prev_pos = null;
	}
	
	private NavigationStepResult navStepResult() throws Exception {
		return new NavigationStepResult(this.mm.position(), this.mm.direction(), this.path.remaining_list(),
				this.destinations.prev_list(), this.destinations.remaining_list(), this.mm.mapView());
	}

	public NavigationStepResult navigate() throws Exception {
		
		Pair current_dst = this.destinations.current();
		if (current_dst == null) { // No more remaining destinations, we're done exploring all of them
			return this.navStepResult();
		}
		
		switch (this.state) {
		case NOT_NAVIGATING:
		case MOVED:
			if (this.mm.markBlobs()) {
				this.state = State.DETECTED_BLOB;
				return this.navStepResult();
			}
			// intentional fall-through
		case DETECTED_BLOB:
			Pair current_pos = this.mm.position();
			if (current_dst.equals(current_pos)) {
				// Our current path leads to our current destination, which we've reached.
				// Clear our path, to signal to later code that a new path must be planned.
				this.path.clear();
				this.destinations.removePos(0);
				// Loop, because some madman might have queued the same position
				// multiple times consecutively.
				do {
					this.destinations.advance();
					current_dst = this.destinations.current();
					if (current_dst == null) { // Last destination reached.
						this.state = State.NOT_NAVIGATING;
						return this.navStepResult();
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
			
		case DETECTED_HAZARD:
			current_pos = this.mm.position();
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
			if (!this.mm.direction().equals(step_direction)) {
				this.turnTo(step_direction);
				this.mm.setDirection(step_direction);
				this.state = State.TURNED;
				return this.navStepResult();
			}
			// intentional fall-through
		case TURNED:
			if (!this.mm.checkHazard()) {
				this.prev_pos = this.mm.position();
				this.mm.invalidatePosition();
				this.moveForward();
				this.state = State.MOVED;
				return this.navStepResult();
			}

			// There was a hazard in front of us, and we couldn't go forward.
			// Plan a new path.
			this.planPath();

			if (this.path == null) {
				// Another unreachable destination case. Let's be consistent and return null
				// here too.
				this.state = State.NOT_NAVIGATING;
				return this.navStepResult();
			}
			// Advance to grab the proper next position.
			this.path.advance();
			this.state = State.DETECTED_HAZARD;
			return this.navStepResult();
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

	private List<Pair> planPath() throws Exception {
		Pair current_pos = this.mm.position();
		Pair destination = this.destinations.current();
		MapView map = this.mm.mapView();

		List<Pair> path = PathFindingAlgorithm.run(current_pos, destination, map);
		this.path = new PositionList(path);
		return this.path.view();
	}

}
