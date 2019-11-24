package navigation;

public class PositionList {
	private Pair[] pos;
	private int next_idx;
	
	public PositionList() { }
	
	public PositionList(Pair[] pos) { 
		this();
	}
	
	public void add_pos(int idx, Pair pos) { }
	
	public Pair remove_pos(int idx) { }
	
	public Pair[] view() { }
	
	public Pair current() { }
	
	public void next() { }
}
