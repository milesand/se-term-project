package map;

import utility.OptionalBool;

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
