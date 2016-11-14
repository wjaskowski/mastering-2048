package put.ci.cevo.experiments.connect4.mappers;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.games.connect4.players.Connect4NTuplesPlayer;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.encodings.ntuple.NTuples;

/**
 * Default mapper relying solely on our implementation of Connect4. Uses {@link Connect4Player} as opposed to
 * {@link put.ci.cevo.games.connect4.players.Connect4NTuplesThillCompatibleAgent} required to run Thill's code.
 */
public final class NTuplesConnect4PlayerMapper implements GenotypePhenotypeMapper<NTuples, Connect4Player> {

	@Override
	public Connect4Player getPhenotype(NTuples genotype, RandomDataGenerator random) {
		return new Connect4NTuplesPlayer(genotype);
	}

}
