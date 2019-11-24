package sim;

public enum Direction {
	N {
		public int x() {
			return 0;
		};

		public int y() {
			return 1;
		}

		@Override
		public Direction next_clockwise() {
			return E;
		}
	},
	E {
		public int x() {
			return 1;
		};

		public int y() {
			return 0;
		}

		@Override
		public Direction next_clockwise() {
			return S;
		}
	},
	S {
		public int x() {
			return 0;
		};

		public int y() {
			return -1;
		}

		@Override
		public Direction next_clockwise() {
			return W;
		}
	},
	W {
		public int x() {
			return -1;
		};

		public int y() {
			return 0;
		}

		@Override
		public Direction next_clockwise() {
			return N;
		}
	};

	public abstract int x();

	public abstract int y();

	public abstract Direction next_clockwise();
}
