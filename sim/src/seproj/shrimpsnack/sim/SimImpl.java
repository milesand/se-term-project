package seproj.shrimpsnack.sim;

import java.util.stream.Stream;
import java.util.Random;

public class SimImpl implements SimInterface {
	// Position and direction of robot.
	private int x;
	private int y;
	private Direction direction;

	// Map (duh).
	private final Map map;

	// RNG for simulating imperfect motion.
	private final Random rng;

	// Boundary values for determining imperfect motion occurrence.
	// On each move_front call, rng.nextFloat() is called; If the result is
	// less than ipm_0_boundary, the robot does not move. if the result is larger
	// than ipm_2_boundary, the robot moves two cells forward (if possible).
	private float ipm0Boundary;
	private float ipm2Boundary;

	// Construction uses builder pattern, to make user code easier to read..
	// Note that there are no public constructors; Builder is the only way to create
	// SimImpl outside this class.
	private SimImpl(Builder builder) {
		// Set robot's position and direction.
		this.x = builder.x;
		this.y = builder.y;
		this.direction = builder.direction;

		// Create map.
		this.map = new Map(builder.mapWidth, builder.mapHeight);

		// Iterate through hazard coordinates, and mark them on the map appropriately.
		// If the coordinate sequence is null, consider it empty.
		if (builder.hazards != null) {
			builder.hazards.forEach(coord -> { // type of coord is Coordinates, defined below.
				// It's an error if robot's starting position is Hazard.
				if (coord.x == this.x && coord.y == this.y) {
					throw new IllegalArgumentException(
							String.format("Overlapping coordinates (%d, %d) for hazard and initial robot position",
									coord.x, coord.y));
				}
				this.map.set(coord.x, coord.y, Cell.HAZARD);
			});
		}

		// Same for blobs. If any overlap with hazard, it's an error.
		// It's fine if it overlaps with robot's initial position, though.
		if (builder.blobs != null) {
			builder.blobs.forEach(coord -> {
				if (this.map.get(coord.x, coord.y).equals(Cell.HAZARD)) {
					throw new IllegalArgumentException(String
							.format("Overlapping coordinates (%d, %d) for hazards and color blobs", coord.x, coord.y));
				}
				this.map.set(x, y, Cell.COLOR_BLOB);
			});
		}

		if (builder.rng == null) {
			this.rng = new Random();
		} else {
			this.rng = builder.rng;
		}

		this.ipm0Boundary = builder.ipm0Probability;
		this.ipm2Boundary = 1.0f - builder.ipm2Probabilty;
	}

	// Implementation of SimInterface.
	// For the description of these methods, see the interface side's comments.

	@Override
	public int x() {
		return this.x;
	}

	@Override
	public int y() {
		return this.y;
	}

	@Override
	public boolean moveForward() {
		// coordinates of target cell.
		int targetX = this.x + this.direction.x();
		int targetY = this.y + this.direction.y();

		if (this.hazardOrOob(targetX, targetY)) {
			return false;
		}

		// Get a random number in [0.0f, 1.0f)...
		float r = this.rng.nextFloat();

		// If not larger than ipm_0_boundary, do nothing.
		if (r > this.ipm0Boundary) {
			if (r > this.ipm2Boundary) {
				// Check the cell two cells forward. If not OOB or Hazard,
				// Move to that cell.
				int impX = targetX + this.direction.x();
				int impY = targetY + this.direction.y();
				if (!this.hazardOrOob(impX, impY)) {
					targetX = impX;
					targetY = impY;
				}
			}
			this.x = targetX;
			this.y = targetY;
		}
		return true;
	}

	@Override
	public void turnClockwise() {
		this.direction = this.direction.nextClockwise();
	}

	@Override
	public boolean detectHazard() {
		// NOTE: we'll consider OOB case a hazard, since
		// they're pretty much the same in that it's an error to
		// try to move the robot to that cell.
		return this.hazardOrOob(this.x + this.direction.x(), this.y + this.direction.y());
	}

	@Override
	public boolean[] detectBlobs() {
		boolean[] ret = new boolean[4];
		Direction d = Direction.N;
		for (int i = 0; i < 4; i++) {
			int adjX = this.x + d.x();
			int adjY = this.y + d.y();
			try {
				ret[i] = this.map.get(adjX, adjY).equals(Cell.COLOR_BLOB);
			} catch (IndexOutOfBoundsException e) {
				ret[i] = false;
			}
			d = d.nextClockwise();
		}
		return ret;
	}

	// End of SimInterface Implementation

	// Checks if given coordinate is a hazard or out-of-bound.
	// Useful for checking whether the robot can move into that cell.
	private boolean hazardOrOob(int x, int y) {
		try {
			return this.map.get(x, y).equals(Cell.HAZARD);
		} catch (IndexOutOfBoundsException e) {
			return true;
		}
	}

	// Returns the direction the robot is facing.
	// This method is not specified in the specification and SimInterface, but it's
	// useful nonetheless.
	public Direction direction() {
		return this.direction;
	}

	// Returns the map's width.
	// This method is not specified in the specification and SimInterface, but it's
	// useful nonetheless.
	public int map_width() {
		return this.map.width();
	}

	// Returns the map's height.
	// This method is not specified in the specification and SimInterface, but it's
	// useful nonetheless.
	public int map_height() {
		return this.map_height();
	}

	// A pair of x coordinate and y coordinate.
	// Used for passing in hazard and color blob coordinates via Stream.
	public static class Coordinates {
		private final int x;
		private final int y;

		public Coordinates(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	// Builder class for SimImpl.
	public static class Builder {
		// Width and Height for the map, and a flag whether these have been set.
		private int mapWidth;
		private int mapHeight;
		private boolean mapSizeSet;

		private int x;
		private int y;
		private boolean robotPositionSet;

		private Direction direction;

		private Stream<Coordinates> hazards;
		private Stream<Coordinates> blobs;

		// RNG for simulating imperfect motion.
		private Random rng;
		// Probability of imperfect motion where move_forward doesn't move the robot
		// forward.
		private float ipm0Probability = 0.1f;
		// Probability of imperfect motion where move_forward moves the robot two cells
		// forward.
		private float ipm2Probabilty = 0.1f;

		public Builder() {
			// Note: default values are false for boolean,
			// null for non-primitives. 0 for int, but that's irrelevant.
		}

		// Set the size of the map.
		public Builder mapSize(int mapWidth, int mapHeight) {
			if (mapWidth <= 0 || mapHeight <= 0) {
				throw new IllegalArgumentException(String.format("Illegal Map Size: (%d, %d)", mapWidth, mapHeight));
			}
			this.mapWidth = mapWidth;
			this.mapHeight = mapHeight;
			this.mapSizeSet = true;
			return this;
		}

		// Set the robot's initial position.
		public Builder robotPosition(int x, int y) {
			if (x < 0) {
				throw new IllegalArgumentException(String.format("Negative initial x coordinate: %d", x));
			}
			if (y < 0) {
				throw new IllegalArgumentException(String.format("Negative initial y coordinate: %d", y));
			}
			this.x = x;
			this.y = y;
			this.robotPositionSet = true;
			return this;
		}

		// Set the initial direction the robot is facing.
		public Builder robotDirection(Direction direction) {
			this.direction = direction;
			return this;
		}

		// Set the list of hazards. If not set, no hazards will be placed.
		public Builder hazards(Stream<Coordinates> hazards) {
			this.hazards = hazards;
			return this;
		}

		// Set the list of color blobs. If not set, no color blobs will be placed.
		public Builder blobs(Stream<Coordinates> blobs) {
			this.blobs = blobs;
			return this;
		}

		// Set the RNG for simulating imperfect motion.
		public Builder rng(Random rng) {
			this.rng = rng;
			return this;
		}

		// Set the probability of imperfect motion where move_forward doesn't move the
		// robot happening.
		// Default value is 0.1 (10%).
		public Builder setNoMovementProbability(float prob) {
			if (prob < 0.0f || 1.0f < prob) {
				throw new IllegalArgumentException(String.format("Invalid probability: %f", prob));
			}
			this.ipm0Probability = prob;
			return this;
		}

		// Set the probability of imperfect motion where move_forward doesn't move the
		// robot happening.
		// Default value is 0.1 (10%).
		public Builder setDoubleForwardProbability(float prob) {
			if (prob < 0.0f || 1.0f < prob) {
				throw new IllegalArgumentException(String.format("Invalid probability: %f", prob));
			}
			this.ipm2Probabilty = prob;
			return this;
		}

		public SimImpl build() {
			if (!this.mapSizeSet) {
				throw new IllegalStateException("Size of the map has not been set");
			}

			if (!this.robotPositionSet) {
				throw new IllegalStateException("Position of the robot has not been set");
			}

			if (this.direction == null) {
				throw new IllegalStateException("Direction the robot is facing has not been set");
			}

			if (this.mapWidth <= this.x || this.mapHeight <= this.y) {
				throw new IndexOutOfBoundsException(
						String.format("Coordinates (%d, %d) out of bounds for map size (%d, %d)", this.x, this.y,
								this.mapWidth, this.mapHeight));
			}

			if (this.ipm0Probability > 1.0f - this.ipm2Probabilty) {
				throw new IllegalArgumentException(String.format("Invalid sum for imperfect motion probabilities: %f",
						this.ipm0Probability + this.ipm2Probabilty));
			}

			return new SimImpl(this);
		}
	}
}
