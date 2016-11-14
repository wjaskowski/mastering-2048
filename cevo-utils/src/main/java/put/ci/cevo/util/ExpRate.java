package put.ci.cevo.util;

import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * Models exponential growth/decay.
 */
public class ExpRate {

	private final double rate;
	private final double initialValue;

	public ExpRate(double initialValue, double targetValue, double targetValueTime) {
		this(computeRate(initialValue, targetValue, targetValueTime), initialValue);
	}

	public ExpRate(double rate, double initialValue) {
		this.rate = rate;
		this.initialValue = initialValue;
	}

	public double computeValue(double time) {
		return initialValue * exp(rate * time);
	}

	private static double computeRate(double initialValue, double finalValue, double time) {
		return log(finalValue / initialValue) / time;
	}

}
