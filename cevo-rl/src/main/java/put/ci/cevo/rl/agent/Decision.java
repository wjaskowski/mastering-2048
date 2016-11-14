package put.ci.cevo.rl.agent;

import com.google.common.base.Preconditions;
import put.ci.cevo.rl.environment.Action;

/** Decision = action + (some) value of this action */
public class Decision<A extends Action> {
	private final A action;
	private final double value;

	private Decision(A action, double value) {
		Preconditions.checkNotNull(action);
		this.action = action;
		this.value = value;
	}

	/** Decision with unknown value. TODO: Represent unknown decision differently than NaN?  */
	public static <A extends Action> Decision<A> of(A action) {
		return of(action, Double.NaN);
	}

	public static <A extends Action> Decision<A> of(A action, double expectedValue) {
		return new Decision<>(action, expectedValue);
	}

	public A getAction() {
		return action;
	}

	public double getValue() {
		return value;
	}
}
