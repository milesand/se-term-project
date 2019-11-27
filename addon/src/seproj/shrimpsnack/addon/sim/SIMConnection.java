package seproj.shrimpsnack.addon.sim;

import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public interface SIMConnection {
	boolean detectHazard();

	boolean[] detectBlob();

	Pair getPosition();

	Direction getDirection();

	Pair getSize();

	void moveForward();

	void turn();
}
