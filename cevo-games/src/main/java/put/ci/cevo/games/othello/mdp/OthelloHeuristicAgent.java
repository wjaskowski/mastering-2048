package put.ci.cevo.games.othello.mdp;

import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.games.GameQFunctionPolicy;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.Decision;
import put.ci.cevo.rl.agent.QFunctionAgent;
import put.ci.cevo.rl.evaluation.StateActionValueFunctionOld;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.agent.policies.QFunctionControlPolicy;

public class OthelloHeuristicAgent implements Agent<OthelloState,OthelloMove> {

	QFunctionAgent<OthelloState, OthelloMove> innerAgent;

	private static double[] weights = { 1.00f, -0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f, -0.25f, -0.25f,
			0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f, 0.10f, 0.01f, 0.05f, 0.02f, 0.02f, 0.05f, 0.01f, 0.10f, 0.05f,
			0.01f, 0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f, 0.05f, 0.01f, 0.02f, 0.01f, 0.01f, 0.02f, 0.01f, 0.05f,
			0.10f,
			0.01f, 0.05f, 0.02f, 0.02f, 0.05f, 0.01f, 0.10f, -0.25f, -0.25f, 0.01f, 0.01f, 0.01f, 0.01f, -0.25f, -0.25f,
			1.00f, -0.25f, 0.10f, 0.05f, 0.05f, 0.10f, -0.25f, 1.00f };

	public OthelloHeuristicAgent() {
		innerAgent = new QFunctionAgent<>(new StateActionValueFunctionOld<>(new WPC(
				weights), new OthelloSelfPlayEnvironment()), new GameQFunctionPolicy<>(0));
	}

	public OthelloHeuristicAgent(QFunctionControlPolicy<OthelloState, OthelloMove> policy) {
		innerAgent = new QFunctionAgent<>(new StateActionValueFunctionOld<>(new WPC(weights),
				new OthelloSelfPlayEnvironment()), policy);
	}

	@Override
	public Decision<OthelloMove> chooseAction(OthelloState state, List<OthelloMove> availableActions,
			RandomDataGenerator random) {
		return innerAgent.chooseAction(state, availableActions, random);
	}
}
