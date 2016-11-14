package put.ci.cevo.experiments.rl;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.GameQFunctionPolicy;
import put.ci.cevo.games.TwoPlayerGameState;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.QFunctionAgent;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.evaluation.StateActionValueFunctionOld;
import put.ci.cevo.rl.evaluation.ActionValueFunction;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class RealFunctionGameAgentMapping<S extends TwoPlayerGameState, A extends Action> implements
		GenotypePhenotypeMapper<RealFunction, Agent<S, A>> {

	private final Environment<S, A> environmentModel;
	private final QFunctionControlPolicy<S, A> policy;

	@AccessedViaReflection
	public RealFunctionGameAgentMapping(Environment<S, A> environmentModel) {
		this(environmentModel, new GameQFunctionPolicy<>(0));
	}

	@AccessedViaReflection
	public RealFunctionGameAgentMapping(Environment<S, A> environmentModel, QFunctionControlPolicy<S, A> policy) {
		this.environmentModel = environmentModel;
		this.policy = policy;
	}

	@Override
	public Agent<S, A> getPhenotype(RealFunction function, RandomDataGenerator random) {
		ActionValueFunction<S, A> actionValueFunction = new StateActionValueFunctionOld<S, A>(function, environmentModel);
		return new QFunctionAgent<S, A>(actionValueFunction, policy);
	}

	public static <S extends TwoPlayerGameState, A extends Action> QFunctionAgent<S, A> getGamePlayingAgent(
			Environment<S, A> environmentModel, RealFunction function) {
		ActionValueFunction<S, A> actionValueFunction = new StateActionValueFunctionOld<S, A>(function, environmentModel);
		QFunctionControlPolicy<S, A> policy = new GameQFunctionPolicy<>(0);
		return new QFunctionAgent<>(actionValueFunction, policy);
	}
}
