package put.ci.cevo.games.dct;

public class DCTParams {

	private final int timeSteps;
	private final int radius;
	private final int testLength;

	public DCTParams(int timeSteps, int radius, int testLength) {
		this.timeSteps = timeSteps;
		this.radius = radius;
		this.testLength = testLength;
	}

	public int getTimeSteps() {
		return timeSteps;
	}

	public int getRadius() {
		return radius;
	}

	public int getTestLength() {
		return testLength;
	}

	public static DCTParams easyDCT() {
		return new DCTParams(31, 2, 110);
	}

	public static DCTParams mediumDCT() {
		return new DCTParams(59, 3, 110);
	}

	public static DCTParams hardDCT() {
		return new DCTParams(149, 3, 320);
	}
}
