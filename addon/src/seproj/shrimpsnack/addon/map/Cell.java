package seproj.shrimpsnack.addon.map;

import seproj.shrimpsnack.addon.utility.OptionalBool;

public class Cell {
	private OptionalBool hazard;
	private OptionalBool blob;

	public Cell() {
		this.hazard = OptionalBool.Unknown;
		this.blob = OptionalBool.Unknown;
	}

	public void setHazard(boolean hazard) {
		if (hazard) {
			this.hazard = OptionalBool.True;
			this.blob = OptionalBool.False;
		} else {
			this.hazard = OptionalBool.False;
		}
	}

	public void setBlob(boolean blob) {
		if (blob) {
			this.hazard = OptionalBool.False;
			this.blob = OptionalBool.True;
		} else {
			this.blob = OptionalBool.False;
		}
	}

	public OptionalBool isHazard() {
		return this.hazard;
	}

	public OptionalBool isBlob() {
		return this.blob;
	}
}
