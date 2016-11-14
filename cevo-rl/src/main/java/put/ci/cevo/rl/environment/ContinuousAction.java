package put.ci.cevo.rl.environment;

import java.io.Serializable;

public class ContinuousAction implements Action, Serializable {

	private static final long serialVersionUID = 5524203975552973532L;

	private double value;

	public ContinuousAction(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

	@Override
	public double[] getDescription() {
		return new double[] { value };
	}

}
