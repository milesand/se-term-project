package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.utility.Pair;

public class MapView {
	private Map inner;

	public MapView(Map inner) {
		this.inner = inner;
	}

	public CellView get(Pair pos) {
		Cell cell = inner.get(pos);
		return new CellView(cell);
	}

	public Pair getSize() {
		return inner.getSize();
	}
}
