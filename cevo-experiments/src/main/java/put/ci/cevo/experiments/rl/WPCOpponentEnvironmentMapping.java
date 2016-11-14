package put.ci.cevo.experiments.rl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.TwoPlayerGameState;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.othello.mdp.GameOpponentEnvironment;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.games.GameState;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class WPCOpponentEnvironmentMapping<S extends TwoPlayerGameState, A extends Action> implements
		GenotypePhenotypeMapper<WPC, List<Environment<S, A>>> {

	private Environment<S, A> env;

	@AccessedViaReflection
	public WPCOpponentEnvironmentMapping(Environment<S, A> environment) {
		this.env = environment;
	}

	@Override
	public List<Environment<S, A>> getPhenotype(WPC genotype, RandomDataGenerator random) {
		List<Environment<S, A>> result = new ArrayList<>();
		Agent<S, A> agent = RealFunctionGameAgentMapping.getGamePlayingAgent(env, genotype);
		result.add(new GameOpponentEnvironment<S, A>(env, agent, Board.WHITE));
		result.add(new GameOpponentEnvironment<S, A>(env, agent, Board.BLACK));
		return result;
	}
}
