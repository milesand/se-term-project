package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.utility.OptionalBool;

public class CellView {
	private Cell inner;

	public CellView(Cell inner) {
		this.inner = inner;
	}

	public OptionalBool isHazard() {
		return inner.isHazard();
	}

	public OptionalBool isBlob() {
		return inner.isBlob();
	}
}
