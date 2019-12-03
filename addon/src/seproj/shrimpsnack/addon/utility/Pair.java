package seproj.shrimpsnack.addon.utility;

public class Pair {
	public final int x;
	public final int y;

	public Pair(int x, int y) {
		this.x = x;
		this.y = y;
	}
	@Override
	public boolean equals(Object other) {
	    if (this == other)
	        return true;
	    if (other == null)
	        return false;
	    if (getClass() != other.getClass())
	        return false;
	    Pair otherp = (Pair) other;
	    // field comparison
	    return this.x == otherp.x && this.y == otherp.y;
	}
}
