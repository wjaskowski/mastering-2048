package put.ci.cevo.experiments.connect4.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.connect4.players.Connect4NTuplesThillCompatibleAgent;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;

public final class DoubleNTuplesConnect4ThillAgentMapper implements GenotypePhenotypeMapper<DoubleNTuples, Agent> {

	@Override
	public Agent getPhenotype(DoubleNTuples genotype, RandomDataGenerator random) {
		return new Connect4NTuplesThillCompatibleAgent(genotype);
	}

}
