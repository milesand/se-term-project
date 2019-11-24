package sim;

import java.lang.IllegalArgumentException;
import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;

class Map {
	private ArrayList<Cell> cells;
	private int width;
	private int height;

	// Initialize Map of empty cells, with given width and height.
	Map(int width, int height) {

		if (width <= 0) {
			throw new IllegalArgumentException(String.format("Illegal Width: %d", width));
		}

		if (height <= 0) {
			throw new IllegalArgumentException(String.format("Illegal Height: %d", height));
		}

		this.width = width;
		this.height = height;
		this.cells = new ArrayList<Cell>(width * height);

		while (this.cells.size() != width * height) {
			this.cells.add(Cell.EMPTY);
		}
	}

	// Turn a coordinate into an index for internal use.
	// Throws IndexOutOfBounds if either component of the coordinate is out of
	// bounds.
	private int index(int x, int y) {
		if (x < 0 || this.width <= x || y < 0 || this.height <= y) {
			throw new IndexOutOfBoundsException(String
					.format("Coordinates (%d, %d) out of bounds for map size (%d, %d)", x, y, this.width, this.height));
		}

		return y * this.width + x;
	}

	// Returns a cell state for given coordinate.
	//
	public Cell get(int x, int y) {
		return this.cells.get(this.index(x, y));
	}

	public void set(int x, int y, Cell cell) {
		this.cells.set(this.index(x, y), cell);
	}

	public int width() {
		return this.width;
	}

	public int height() {
		return this.height;
	}
}
