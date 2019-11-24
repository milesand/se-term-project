package map;

public class MapView {
	private Map inner;
	
	public MapView(Map inner) {
		this.inner = inner;
	}
	
	public CellView get(Pair pos) {
		Cell cell = inner.get(pos);
		return new CellView(cell);
	}
	
	public Pair get_size() {
		return inner.get_size();
	}
}
