package map;

import utility.OptionalBool;

public class Cell {
	private OptionalBool hazard;
	private OptionalBool blob;

	public Cell() {
		this.hazard = OptionalBool.False;
		this.blob = OptionalBool.False;
	}

	public void setHazard() {
		this.hazard = OptionalBool.True;
	}

	public void setBlob() {
		this.blob = OptionalBool.True;
	}

	public OptionalBool isHazard() {
		return hazard;
	}

	public OptionalBool isBlob() {
		return blob;
	}

	public Cell clone() {
		Cell cell = new Cell();
		cell.hazard = this.hazard;
		cell.blob = this.blob;
		return cell;
	}
}
