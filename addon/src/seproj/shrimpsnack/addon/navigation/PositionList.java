package seproj.shrimpsnack.addon.navigation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import seproj.shrimpsnack.addon.utility.Pair;

public class PositionList {
	private ArrayList<Pair> pos;
	private int next_idx;

	public PositionList() {
		this.pos = new ArrayList<Pair>();
		this.next_idx = 0;
	}

	public PositionList(Collection<Pair> pos) {
		this.pos = new ArrayList<Pair>(pos);
		this.next_idx = 0;

	}

	public void addPos(int idx, Pair pos) {
		this.pos.add(idx, pos);
	}

	public Pair removePos(int idx) {
		return this.pos.remove(idx);
	}

	public List<Pair> view() {
		return Collections.unmodifiableList(this.pos);
	}

	public Pair current() {
		if (this.next_idx == this.pos.size()) {
			return null;
		}
		return this.pos.get(this.next_idx);
	}

	public void advance() {
		if (this.next_idx != this.pos.size()) {
			this.next_idx++;
		}
	}
}
