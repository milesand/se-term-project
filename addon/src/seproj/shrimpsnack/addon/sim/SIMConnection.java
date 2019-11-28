package seproj.shrimpsnack.addon.sim;

import seproj.shrimpsnack.addon.utility.Direction;
import seproj.shrimpsnack.addon.utility.Pair;

public interface SIMConnection {
	boolean detectHazard() throws Exception;

	boolean[] detectBlob() throws Exception;

	Pair getPosition() throws Exception;

	Direction getDirection() throws Exception;

	Pair getSize() throws Exception;

	void moveForward() throws Exception;

	void turn() throws Exception;
}
