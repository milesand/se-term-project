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
		public Direction nextClockwise() {
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
		public Direction nextClockwise() {
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
		public Direction nextClockwise() {
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
		public Direction nextClockwise() {
			return N;
		}
	};

	public abstract int x();

	public abstract int y();

	public abstract Direction nextClockwise();
}
