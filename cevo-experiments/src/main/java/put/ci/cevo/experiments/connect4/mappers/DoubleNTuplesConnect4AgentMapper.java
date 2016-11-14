package put.ci.cevo.experiments.connect4.mappers;

import java.util.function.UnaryOperator;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.connect4.players.Connect4DeltaNTuplesAgent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTupleAgent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.evaluation.ActionValueFunction;

public class DoubleNTuplesConnect4AgentMapper implements GenotypePhenotypeMapper<DoubleNTuples, Agent<Connect4State, Connect4Action>> {

	private final UnaryOperator<ActionValueFunction<Connect4State, Connect4Action>> decorator;

	public DoubleNTuplesConnect4AgentMapper(UnaryOperator<ActionValueFunction<Connect4State, Connect4Action>> decorator) {
		this.decorator = decorator;
	}

	@Override
	public Agent<Connect4State, Connect4Action> getPhenotype(DoubleNTuples doubleNTuples, RandomDataGenerator random) {
		return new DoubleNTupleAgent<>(doubleNTuples, x -> new Connect4DeltaNTuplesAgent(x, decorator));
	}
}
