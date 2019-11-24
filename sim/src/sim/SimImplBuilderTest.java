package sim;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import sim.SimImpl.Builder;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

class SimImplBuilderTest {

	@Test
	void builderThrowsOnUnsetValues() {
		SimImpl.Builder builder = new SimImpl.Builder();
		assertThrows(IllegalStateException.class, () -> builder.build());

		builder.mapSize(7, 9);
		assertThrows(IllegalStateException.class, () -> builder.build());

		builder.robotPosition(3, 4);
		assertThrows(IllegalStateException.class, () -> builder.build());

		builder.robotDirection(Direction.N);
		assertDoesNotThrow(() -> builder.build());
	}

	@RepeatedTest(100)
	void builderThrowsOnInvalidMapSize(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int w = (rep / 10) - 4;
		final int h = (rep % 10) - 4;
		if (w <= 0 || h <= 0) {
			assertThrows(IllegalArgumentException.class, () -> new SimImpl.Builder().mapSize(w, h));
		} else {
			assertDoesNotThrow(() -> new SimImpl.Builder().mapSize(w, h));
		}
	}

	@RepeatedTest(100)
	void builderThrowsOnInvalidRobotPosition(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int x = (rep / 10) - 4;
		final int y = (rep % 10) - 4;
		if (x < 0 || y < 0) {
			assertThrows(IllegalArgumentException.class, () -> new SimImpl.Builder().robotPosition(x, y));
		} else {
			assertDoesNotThrow(() -> new SimImpl.Builder().robotPosition(x, y));
		}
	}

	@RepeatedTest(100)
	void builderThrowsOnOutOfBoundRobotPosition(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int x = rep / 10;
		final int y = rep % 10;
		SimImpl.Builder builder = new SimImpl.Builder().mapSize(5, 5).robotPosition(x, y).robotDirection(Direction.N);
		if (x >= 5 || y >= 5) {
			assertThrows(IndexOutOfBoundsException.class, () -> builder.build());
		} else {
			assertDoesNotThrow(() -> builder.build());
		}
	}
}
