package seproj.shrimpsnack.sim;

public interface SimInterface {
	// SIM simulates a robot, and a map the robot moves in. This interface
	// describes necessary operations such system needs to provide, according
	// to the specification.

	// Returns Robot's current x coordinate.
	int x();

	// Returns Robot's current y coordinate.
	int y();

	// Attempts to move Robot forward.
	// On failure(by getting out of map bound or moving into hazard), robot
	// stays where it was and false is returned. Otherwise, the operation is
	// considered successful and true is returned.
	// Note that "Success" does not necessarily mean that the robot has moved
	// one square forward; It merely indicates that the attempted move was
	// valid. The robot may have stayed in the former cell, or have moved two
	// cells forward, due to 'imperfect motion'. The latter case of imperfect
	// motion will never occur if it would result in the robot getting out of
	// map boundary or moving into a hazard.
	boolean moveForward();

	// Rotates the robot 90 degrees, clockwise.
	void turnClockwise();

	// Returns true if the forward cell is HAZARD.
	boolean detectHazard();

	// Returns 4 booleans each indicating whether the north, east, south, and
	// west cells are color blobs.
	boolean[] detectBlobs();
}
