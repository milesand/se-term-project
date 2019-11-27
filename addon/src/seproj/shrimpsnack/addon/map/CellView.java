package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.utility.OptionalBool;

public class CellView {
	private Cell inner;

	public CellView(Cell inner) {
		this.inner = inner;
	}

	public OptionalBool is_hazard() {
		return inner.isHazard();
	}

	public OptionalBool is_blob() {
		return inner.isBlob();
	}
}
