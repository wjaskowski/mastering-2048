package put.ci.cevo.experiments.tetris;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.tetris.Tetris;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.games.tetris.agents.DeltaNTuplesTetrisAgent;
import put.ci.cevo.rl.agent.Agent;

public class NTuplesToTetrisAgentMapper implements GenotypePhenotypeMapper<NTuples, Agent<TetrisState, TetrisAction>> {

	public NTuplesToTetrisAgentMapper() {
	}

	@Override
	public Agent<TetrisState, TetrisAction> getPhenotype(NTuples genotype, RandomDataGenerator random) {
		return new DeltaNTuplesTetrisAgent(genotype);
	}
}
