package seproj.shrimpsnack.sim;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import seproj.shrimpsnack.sim.SimImpl.Coordinates;

class SimImplTest {

	@RepeatedTest(100)
	void initialization(RepetitionInfo repetitionInfo) {
		Random random = new Random((long)repetitionInfo.getCurrentRepetition());
		int width = random.nextInt(31) + 1;
		int height = random.nextInt(31) + 1;
		int x = random.nextInt(width);
		int y = random.nextInt(height);
		Direction dir = Direction.values()[random.nextInt(4)];
				
		SimImpl sim = new SimImpl.Builder()
				.mapSize(width, height)
				.robotPosition(x, y)
				.robotDirection(dir)
				.build();
		
		assertEquals(width, sim.map_width());
		assertEquals(height, sim.map_height());
		assertEquals(x, sim.x());
		assertEquals(y, sim.y());
		assertEquals(dir, sim.direction());
	}
	
	@RepeatedTest(16)
	void moveForward(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final Direction dir = Direction.values()[rep % 4];
		final int x = rep / 8;
		final int y = (rep / 4) % 2;
		
		SimImpl sim = new SimImpl.Builder()
				.mapSize(2, 2)
				.robotPosition(x, y)
				.robotDirection(dir)
				.setNoMovementProbability(0.0f)
				.build();
		
		final int target_x = x + dir.x();
		final int target_y = y + dir.y();
				
		if (0 <= target_x && target_x < 2 && 0 <= target_y && target_y < 2) {
			assertTrue(sim.moveForward());
			assertEquals(sim.x(), target_x);
			assertEquals(sim.y(), target_y);
		} else {
			assertFalse(sim.moveForward());
		}
	}
	
	@RepeatedTest(4)
	void turnClockwise(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		Direction dir = Direction.values()[rep % 4];
		SimImpl sim = new SimImpl.Builder()
				.mapSize(1, 1)
				.robotPosition(0, 0)
				.robotDirection(dir)
				.build();
		for (int i = 0; i < 4; i++) {
			sim.turnClockwise();
			assertEquals(sim.direction(), dir.nextClockwise());
			dir = dir.nextClockwise();
		}
	}
	
	@RepeatedTest(8)
	void detectHazard(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		Direction dir = Direction.values()[rep % 4];
		boolean setHazard = rep / 4 == 0;
		
		SimImpl.Builder builder = new SimImpl.Builder()
				.mapSize(3, 3)
				.robotPosition(1, 1)
				.robotDirection(dir);
		if (setHazard) {
			Coordinates hazard = new Coordinates(1 + dir.x(), 1 + dir.y());
			builder.hazards(Arrays.stream(new Coordinates[] {hazard}));
		}
		
		SimImpl sim = builder.build();
		
		if (setHazard) {
			assertTrue(sim.detectHazard());
		} else {
			assertFalse(sim.detectHazard());
		}
	}
	
	@RepeatedTest(16)
	void detectBlobs(RepetitionInfo repetitionInfo) {
		final int rep = repetitionInfo.getCurrentRepetition() - 1;
		final int blobBits = rep;
		int blobBits_ = blobBits;
		ArrayList<Coordinates> blobs = new ArrayList<Coordinates>();
		boolean[] expected = {false, false, false, false};
		
		for (Direction d : Direction.values()) {
			if ((blobBits_ & 1) == 1) {
				expected[d.ordinal()] = true;
				blobs.add(new Coordinates(1 + d.x(), 1 + d.y()));
			}
			blobBits_ = blobBits_ >> 1;
		}
		
		SimImpl sim = new SimImpl.Builder()
				.mapSize(3, 3)
				.robotPosition(1, 1)
				.robotDirection(Direction.N)
				.blobs(blobs.stream())
				.build();
		
		assertArrayEquals(expected, sim.detectBlobs());
	}
	
	@Test
	void mapBoundaryIsNotBlob() {
		SimImpl sim = new SimImpl.Builder()
				.mapSize(3, 2)
				.robotPosition(1, 0)
				.robotDirection(Direction.N)
				.blobs(Arrays.stream(new Coordinates[] {
						new Coordinates(0, 0), new Coordinates(2, 0)}))
				.build();
		
		assertArrayEquals(new boolean[] {false, true, false, true}, sim.detectBlobs());
	}
	
	@RepeatedTest(100)
	void noMovementMotionError(RepetitionInfo repetitionInfo) {
		Random random = new Random((long)repetitionInfo.getCurrentRepetition());
		SimImpl sim = new SimImpl.Builder()
				.mapSize(1, 3)
				.robotPosition(0, 0)
				.robotDirection(Direction.N)
				.rng(random)
				.setNoMovementProbability(1.0f)
				.setDoubleForwardProbability(0.0f)
				.build();
		
		sim.moveForward();
		
		assertEquals(0, sim.x());
		assertEquals(0, sim.y());
	}
	
	@RepeatedTest(100)
	void DoubleForwardMotionError(RepetitionInfo repetitionInfo) {
		Random random = new Random((long)repetitionInfo.getCurrentRepetition());
		SimImpl sim = new SimImpl.Builder()
				.mapSize(1, 3)
				.robotPosition(0, 0)
				.robotDirection(Direction.N)
				.rng(random)
				.setNoMovementProbability(0.0f)
				.setDoubleForwardProbability(1.0f)
				.build();
		
		sim.moveForward();
		
		assertEquals(0, sim.x());
		assertEquals(2, sim.y());
	}
	
	@RepeatedTest(100)
	void DoubleForwardMotionErrorExceptToHazard(RepetitionInfo repetitionInfo) {
		Random random = new Random((long)repetitionInfo.getCurrentRepetition());
		SimImpl sim = new SimImpl.Builder()
				.mapSize(1, 3)
				.robotPosition(0, 0)
				.robotDirection(Direction.N)
				.hazards(Arrays.stream(new Coordinates[] {
						new Coordinates(0, 2)}))
				.rng(random)
				.setNoMovementProbability(0.0f)
				.setDoubleForwardProbability(1.0f)
				.build();
		
		sim.moveForward();
		
		assertEquals(0, sim.x());
		assertEquals(1, sim.y());
	}
	
	@RepeatedTest(100)
	void DoubleForwardMotionErrorExceptToOOB(RepetitionInfo repetitionInfo) {
		Random random = new Random((long)repetitionInfo.getCurrentRepetition());
		SimImpl sim = new SimImpl.Builder()
				.mapSize(1, 2)
				.robotPosition(0, 0)
				.robotDirection(Direction.N)
				.rng(random)
				.setNoMovementProbability(0.0f)
				.setDoubleForwardProbability(1.0f)
				.build();
		
		sim.moveForward();
		
		assertEquals(0, sim.x());
		assertEquals(1, sim.y());
	}
}
