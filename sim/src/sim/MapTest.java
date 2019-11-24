package sim;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

class MapTest {

	// Used for single-map tests. Arbitrarily chosen.
	final int TEST_MAP_WIDTH = 6;
	final int TEST_MAP_HEIGHT = 7;

	@RepeatedTest(100)
	void mapConstructionTest(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1; // 0 ~ 99
		final int w = (rep / 10) - 4; // -4 ~ 5
		final int h = (rep % 10) - 4; // ditto
		if (w <= 0 || h <= 0) {
			assertThrows(IllegalArgumentException.class, () -> {
				new Map(w, h);
			});
		} else {
			assertDoesNotThrow(() -> {
				new Map(w, h);
			});
		}
	}

	@RepeatedTest(100)
	void mapSizeTest(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1; // 0 ~ 99
		final int w = (rep / 10) + 1; // 1 ~ 10
		final int h = (rep % 10) + 1; // ditto
		Map map = new Map(w, h);
		assertEquals(w, map.width());
		assertEquals(h, map.height());
	}

	@Test
	void mapGetThrowTest() {
		Map map = new Map(TEST_MAP_WIDTH, TEST_MAP_HEIGHT);
		for (int _x = -3; _x < TEST_MAP_WIDTH + 3; _x++) {
			for (int _y = -3; _y < TEST_MAP_HEIGHT + 3; _y++) {
				final int x = _x;
				final int y = _y;
				if (x < 0 || x >= TEST_MAP_WIDTH || y < 0 || y >= TEST_MAP_HEIGHT) {
					assertThrows(IndexOutOfBoundsException.class, () -> {
						map.get(x, y);
					});
				} else {
					assertDoesNotThrow(() -> {
						map.get(x, y);
					});
				}
			}
		}
	}

	@Test
	void initialMapContentsTest() {
		Map map = new Map(TEST_MAP_WIDTH, TEST_MAP_HEIGHT);
		for (int x = 0; x < TEST_MAP_WIDTH; x++) {
			for (int y = 0; y < TEST_MAP_HEIGHT; y++) {
				assertEquals(map.get(x, y), Cell.EMPTY);
			}
		}
	}

	@Test
	void mapSetThrowTest() {
		Map map = new Map(TEST_MAP_WIDTH, TEST_MAP_HEIGHT);
		for (int _x = -3; _x < TEST_MAP_WIDTH + 3; _x++) {
			for (int _y = -3; _y < TEST_MAP_HEIGHT + 3; _y++) {
				final int x = _x;
				final int y = _y;
				if (x < 0 || x >= TEST_MAP_WIDTH || y < 0 || y >= TEST_MAP_HEIGHT) {
					for (Cell _c : Cell.values()) {
						final Cell c = _c;
						assertThrows(IndexOutOfBoundsException.class, () -> {
							map.set(x, y, c);
						});
					}
				} else {
					for (Cell _c : Cell.values()) {
						final Cell c = _c;
						assertDoesNotThrow(() -> {
							map.set(x, y, c);
						});
					}
				}
			}
		}
	}

	@RepeatedTest(100)
	void mapGetSetTest(RepetitionInfo repetitionInfo) {
		Map map = new Map(TEST_MAP_WIDTH, TEST_MAP_HEIGHT);
		Cell[] cells = Cell.values();
		final long SEED = (long) repetitionInfo.getCurrentRepetition();

		Random rng = new Random(SEED);
		for (int x = 0; x < TEST_MAP_WIDTH; x++) {
			for (int y = 0; y < TEST_MAP_HEIGHT; y++) {
				int i = rng.nextInt(cells.length);
				map.set(x, y, cells[i]);
			}
		}

		rng.setSeed(SEED);
		for (int x = 0; x < TEST_MAP_WIDTH; x++) {
			for (int y = 0; y < TEST_MAP_HEIGHT; y++) {
				int i = rng.nextInt(cells.length);
				assertEquals(map.get(x, y), cells[i]);
			}
		}
	}
}
