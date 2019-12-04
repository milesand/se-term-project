package seproj.shrimpsnack.sim;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import seproj.shrimpsnack.sim.Direction;
import seproj.shrimpsnack.sim.SimImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

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
	
	@RepeatedTest(100)
	void builderThrowsOnInvalid0ErrorProbability(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition();
		float prob = (float)rep / 50.0f - 0.5f;
		SimImpl.Builder builder = new SimImpl.Builder();
		if (prob < 0.0f || prob > 1.0f) {
			assertThrows(IllegalArgumentException.class, () -> builder.setNoMovementProbability(prob));
		} else {
			assertDoesNotThrow(() -> builder.setNoMovementProbability(prob));
		}
	}
	
	@RepeatedTest(100)
	void builderThrowsOnInvalid2ErrorProbability(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition();
		float prob = (float)rep / 50.0f - 0.5f;
		SimImpl.Builder builder = new SimImpl.Builder();
		if (prob < 0.0f || prob > 1.0f) {
			assertThrows(IllegalArgumentException.class, () -> builder.setDoubleForwardProbability(prob));
		} else {
			assertDoesNotThrow(() -> builder.setDoubleForwardProbability(prob));
		}
	}
	
	@RepeatedTest(100)
	void builderThrowsOnInvalidSummedErrorProbability(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition();
		float prob = (float)rep / 100.0f;
		SimImpl.Builder builder = new SimImpl.Builder()
				.mapSize(1, 1)
				.robotDirection(Direction.N)
				.robotPosition(0, 0)
				.setDoubleForwardProbability(prob)
				.setNoMovementProbability(prob);
		if (prob > 0.5f) {
			assertThrows(IllegalArgumentException.class, () -> builder.build());
		} else {
			assertDoesNotThrow(() -> builder.build());
		}
	}
	
	@RepeatedTest(64)
	void builderThrowsOnInvalidHazardPosition(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int x = rep / 8 - 1;
		final int y = rep % 8 - 1;
		Stream<SimImpl.Coordinates> hazard = Arrays.stream(
				new SimImpl.Coordinates[] {new SimImpl.Coordinates(x, y)});
		SimImpl.Builder builder = new SimImpl.Builder()
				.mapSize(6, 6)
				.robotDirection(Direction.N)
				.robotPosition(0, 0)
				.hazards(hazard);
		if (x < 0 || x >= 6 || y < 0 || y >= 6) {
			assertThrows(IndexOutOfBoundsException.class, () -> builder.build());
		} else if (x == 0 && y == 0) {
			assertThrows(IllegalArgumentException.class, () -> builder.build());
		} else {
			assertDoesNotThrow(() -> builder.build());
		}
	}
	
	@RepeatedTest(64)
	void builderThrowsOnInvalidBlobPosition(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int x = rep / 8 - 1;
		final int y = rep % 8 - 1;
		Stream<SimImpl.Coordinates> blob = Arrays.stream(
				new SimImpl.Coordinates[] {new SimImpl.Coordinates(x, y)});
		SimImpl.Builder builder = new SimImpl.Builder()
				.mapSize(6, 6)
				.robotDirection(Direction.N)
				.robotPosition(0, 0)
				.blobs(blob);
		if (x < 0 || x >= 6 || y < 0 || y >= 6) {
			assertThrows(IndexOutOfBoundsException.class, () -> builder.build());
		} else {
			assertDoesNotThrow(() -> builder.build());
		}
	}
	
	@RepeatedTest(64)
	void builderThrowsOnHazardBlobOverlap(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int hazardBits = rep / 8;
		final int blobBits = rep % 8;
		
		ArrayList<SimImpl.Coordinates> hazards = new ArrayList<SimImpl.Coordinates>();
		int hazardBits_ = hazardBits;
		for (int i = 1; i < 4; i++) {
			if ((hazardBits_ & 1) == 1) {
				hazards.add(new SimImpl.Coordinates(i, 0));
			}
			hazardBits_ = hazardBits_ >> 1;
		}
		
		ArrayList<SimImpl.Coordinates> blobs = new ArrayList<SimImpl.Coordinates>();
		int blobBits_ = blobBits;
		for (int i = 1; i < 4; i++) {
			if ((blobBits_ & 1) == 1) {
				blobs.add(new SimImpl.Coordinates(i, 0));
			}
			blobBits_ = blobBits_ >> 1;
		}
		
		SimImpl.Builder builder = new SimImpl.Builder()
				.mapSize(4, 1)
				.robotPosition(0, 0)
				.robotDirection(Direction.N)
				.hazards(hazards.stream())
				.blobs(blobs.stream());
		
		if ((blobBits & hazardBits) != 0) {
			assertThrows(IllegalArgumentException.class, () -> builder.build());
		} else {
			assertDoesNotThrow(() -> builder.build());
		}
	}
}
