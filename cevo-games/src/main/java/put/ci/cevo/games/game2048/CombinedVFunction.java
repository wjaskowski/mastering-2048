package put.ci.cevo.games.game2048;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;

public class CombinedVFunction<S extends State> implements LearnableStateValueFunction<S> {

	private final List<LearnableStateValueFunction<S>> functions;

	public CombinedVFunction(List<LearnableStateValueFunction<S>> functions) {
		this.functions = functions;
	}

	@Override
	public double getValue(S state) {
		double value = 0;
		for (LearnableStateValueFunction<S> function : functions) {
			value += function.getValue(state);
		}
		return value;
	}

	@Override
	public void increase(S state, double delta) {
		throw new NotImplementedException();
	}

	@Override
	public int getActiveFeaturesCount() {
		int value = 0;
		for (LearnableStateValueFunction<S> function : functions) {
			value += function.getActiveFeaturesCount();
		}
		return value;
	}

	@Override
	public double getActiveWeight(S state, int idx) {
		for (LearnableStateValueFunction<S> function : functions) {
			if (idx < function.getActiveFeaturesCount()) {
				return function.getActiveWeight(state, idx);
			}
			idx -= function.getActiveFeaturesCount();
		}
		throw new IllegalArgumentException("idx");
	}

	@Override
	public void setActiveWeight(S state, int idx, double value) {
		for (LearnableStateValueFunction<S> function : functions) {
			if (idx < function.getActiveFeaturesCount()) {
				function.setActiveWeight(state, idx, value);
				return;
			}
			idx -= function.getActiveFeaturesCount();
		}
		throw new IllegalArgumentException("idx");
	}

	@Override
	public void increaseActiveWeight(S state, int idx, double delta) {
		for (LearnableStateValueFunction<S> function : functions) {
			if (idx < function.getActiveFeaturesCount()) {
				function.increaseActiveWeight(state, idx, delta);
				return;
			}
			idx -= function.getActiveFeaturesCount();
		}
		throw new IllegalArgumentException("idx");
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
