package sim;

import utility.Direction;
import utility.Pair;

public interface SIMConnection {
	boolean detect_hazard();
	boolean[] detect_blob();
	Pair get_position();
	Direction get_direction();
	Pair get_size();
	void move_forward();
	void turn();
}
