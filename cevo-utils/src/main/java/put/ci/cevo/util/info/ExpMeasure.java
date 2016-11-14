package put.ci.cevo.util.info;

public class ExpMeasure {

	private double timeConstant;

	private double numerator;
	private double denominator;

	public ExpMeasure(double timeConstant) {
		this(timeConstant, 0.0);
	}

	public ExpMeasure(double timeConstant, double denominator) {
		this.timeConstant = timeConstant;
		numerator = 0.0;
		this.denominator = denominator;
	}

	public void setTimeConstant(double timeConstant) {
		this.timeConstant = timeConstant;
	}

	public double getTimeConstant() {
		return timeConstant;
	}

	public void update(double deltaT, double value) {
		double eExp = Math.exp(-deltaT / timeConstant);
		numerator = eExp * numerator + value;
		denominator = eExp * denominator + 1.0;
	}

	public double getValue() {
		return numerator / denominator;
	}

}
