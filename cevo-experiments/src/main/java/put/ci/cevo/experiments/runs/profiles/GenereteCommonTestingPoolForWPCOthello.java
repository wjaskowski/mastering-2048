package put.ci.cevo.experiments.runs.profiles;

import org.apache.commons.math3.random.MersenneTwister;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates a set of 1000 WPC individuals used then to evaluated individuals from performance profiles
 */
public final class GenereteCommonTestingPoolForWPCOthello {

	public static void main(String[] args) throws SerializationException {
		UniformRandomPopulationFactory<WPC> populationFactory = new UniformRandomPopulationFactory<>(
			new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -1.0, 1.0));

		// Deterministic random
		final ThreadedRandom globalRandom = new ThreadedRandom(new MersenneTwister(123));
		List<WPC> pool = populationFactory.createPopulation(1000, globalRandom.forThread());

		SerializationManager serializationManager = SerializationManagerFactory.create();
		File output = new File("othello_wpc_pool.dump");
		serializationManager.serialize(new ArrayList<WPC>(pool), output);
	}
}
