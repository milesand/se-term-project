package sim;

public enum Direction {
	N {
		public int x() {
			return 0;
		};

		public int y() {
			return 1;
		}
	},
	E {
		public int x() {
			return 1;
		};

		public int y() {
			return 0;
		}
	},
	S {
		public int x() {
			return 0;
		};

		public int y() {
			return -1;
		}
	},
	W {
		public int x() {
			return -1;
		};

		public int y() {
			return 0;
		}
	};

	public abstract int x();

	public abstract int y();
}
