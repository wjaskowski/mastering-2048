package put.ci.cevo.experiments.connect4.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.connect4.players.Connect4NTuplesPlayer;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;

public final class DoubleNTuplesConnect4PlayerMapper implements GenotypePhenotypeMapper<DoubleNTuples, Connect4Player> {

	@Override
	public Connect4Player getPhenotype(DoubleNTuples genotype, RandomDataGenerator random) {
		return new Connect4NTuplesPlayer(genotype);
	}

}
