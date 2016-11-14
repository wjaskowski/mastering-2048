package put.ci.cevo.experiments.connect4.mappers;

import java.util.function.UnaryOperator;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.connect4.players.Connect4DeltaNTuplesAgent;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

/**
 * Default mapper relying solely on our implementation of Connect4. Uses {@link put.ci.cevo.games.connect4.players.Connect4Player} as opposed to
 * {@link put.ci.cevo.games.connect4.players.Connect4NTuplesThillCompatibleAgent} required to run Thill's code.
 */
public final class NTuplesConnect4AgentMapper
		implements GenotypePhenotypeMapper<NTuples, Agent<Connect4State, Connect4Action>> {

	private final UnaryOperator<ActionValueFunction<Connect4State, Connect4Action>> decorator;

	public NTuplesConnect4AgentMapper(UnaryOperator<ActionValueFunction<Connect4State, Connect4Action>> decorator) {
		this.decorator = decorator;
	}

	@Override
	public Agent<Connect4State, Connect4Action> getPhenotype(NTuples genotype, RandomDataGenerator random) {
		return new Connect4DeltaNTuplesAgent(genotype, decorator);
	}
}
