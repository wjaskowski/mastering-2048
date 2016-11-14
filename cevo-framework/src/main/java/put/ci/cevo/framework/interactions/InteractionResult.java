package put.ci.cevo.framework.interactions;

import java.util.List;

public class InteractionResult {

	private final double firstResult;
	private final double secondResult;

	private final int effort;

	public InteractionResult() {
		this(0, 0, 0);
	}

	public InteractionResult(double firstResult, double secondResult, int effort) {
		this.firstResult = firstResult;
		this.secondResult = secondResult;
		this.effort = effort;
	}

	public double firstResult() {
		return firstResult;
	}

	public double secondResult() {
		return secondResult;
	}

	public int getEffort() {
		return effort;
	}

	public static InteractionResult aggregate(InteractionResult first, InteractionResult second) {
		double firstResult = (first.firstResult + second.firstResult) / 2;
		double secondResult = (first.secondResult + second.secondResult) / 2;
		int effort = first.effort + second.effort;
		return new InteractionResult(firstResult, secondResult, effort);
	}

	public static InteractionResult aggregate(List<InteractionResult> results) {
		double sumFirstResult = 0;
		double sumSecondResult = 0;
		int sumEffort = 0;
		for (InteractionResult result : results) {
			sumFirstResult += result.firstResult;
			sumSecondResult += result.secondResult;
			sumEffort += result.getEffort();
		}

		return new InteractionResult(sumFirstResult / results.size(), sumSecondResult / results.size(), sumEffort);
	}

	public InteractionResult inverted() {
		return new InteractionResult(secondResult, firstResult, effort);
	}

	@Override
	public String toString() {
		return firstResult + ", " + secondResult + ", effort=" + effort;
	}

	public InteractionResult add(InteractionResult result) {
		double firstResult = (this.firstResult + result.firstResult);
		double secondResult = (this.secondResult + result.secondResult);
		int effort = this.effort + result.effort;
		return new InteractionResult(firstResult, secondResult, effort);
	}

	public InteractionResult divide(double divider) {
		return new InteractionResult(this.firstResult / divider, this.secondResult / divider, this.effort);
	}
}
