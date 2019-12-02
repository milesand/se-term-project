package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.utility.Pair;

public class Map {
	private Cell[] cells;
	private Pair size;

	public Map(Pair size) {

		if (size.x <= 0) {
			throw new IllegalArgumentException(String.format("Illegal Width: %d", size.x));
		}

		if (size.y <= 0) {
			throw new IllegalArgumentException(String.format("Illegal Height: %d", size.y));
		}

		this.size = size;
		this.cells = new Cell[size.x * size.y];
		for (int i = 0; i < size.x * size.y; i++) {
			this.cells[i] = new Cell();
		}
	}

	public Cell get(Pair pos) {
		if (pos.x < 0 || this.size.x <= pos.x || pos.y < 0 || this.size.y <= pos.y) {
			throw new IndexOutOfBoundsException(
					String.format("Coordinates (%d, %d) out of bounds for map size (%d, %d)", pos.x, pos.y, this.size.x,
							this.size.y));
		}

		return cells[pos.y * this.size.x + pos.x];
	}

	public Pair getSize() {
		return this.size;
	}

}
