package put.ci.cevo.rl.agent.policies;

import java.util.List;
import java.util.stream.DoubleStream;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.AgentTransition;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.StateValueFunction;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class SoftMaxAfterstatePolicy<S extends State, A extends Action> implements VFunctionControlPolicy<S, A> {

	private static final boolean AVOID_TERMINAL_STATES_FALSE = false;

	// The implementation uses GreedyQFunctionPolicy
	private final boolean avoidTerminalStates;
	private final Environment<S, A> model;
	private double temperature;

	@AccessedViaReflection
	public SoftMaxAfterstatePolicy(Environment<S, A> model, double temperature) {
		this(model, temperature, AVOID_TERMINAL_STATES_FALSE);
	}

	public SoftMaxAfterstatePolicy(Environment<S, A> model, double temperature, boolean avoidTerminalStates) {
		this.model = model;
		this.temperature = temperature;
		this.avoidTerminalStates = avoidTerminalStates;
	}

	@Override
	public Decision<A> chooseAction(S state, List<A> actions, StateValueFunction<S> afterStateVFunction,
			RandomDataGenerator random) {
		Preconditions.checkArgument(actions.size() > 0);

		double[] values = new double[actions.size()];
		for (int i = 0; i < actions.size(); ++i) {
			values[i] = getActionValue(afterStateVFunction, state, actions.get(i));
		}
		//Watchout: using sqrt (nonstardanrd)
		double[] sqrtvalues = DoubleStream.of(values).map(Math::sqrt).toArray();
		double[] probabilities = softmax(sqrtvalues, temperature);

		int idx = RandomUtils.sampleAccordingToDistribution(random, probabilities);
		return Decision.of(actions.get(idx), values[idx]);
	}

	private static double[] softmax(double[] values, double temperature) {
		double max = values[0];
		for (int i = 1; i < values.length; ++i) {
			if (max < values[i]) {
				max = values[i];
			}
		}

		double[] probabilities = new double[values.length];

		double denominator = 0.0;
			for (int i = 0; i < values.length; ++i) {
			probabilities[i] = Math.exp((values[i] - max)/temperature);
			denominator += probabilities[i];
		}


		for (int i = 0; i < values.length; ++i) {
			probabilities[i] /= denominator;
		}

		return probabilities;
	}

	private double getActionValue(StateValueFunction<S> afterStateValueFunction, S state, A action) {
		AgentTransition<S, A> transition = model.getAgentTransition(state, action);
		if (model.isTerminal(transition.getAfterState())) {
			return avoidTerminalStates ? -Double.MAX_VALUE : transition.getReward();
		} else {
			return afterStateValueFunction.getValue(transition.getAfterState()) + transition.getReward();
		}
	}
}
