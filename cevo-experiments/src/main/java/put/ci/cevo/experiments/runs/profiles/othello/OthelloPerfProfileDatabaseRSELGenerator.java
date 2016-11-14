package put.ci.cevo.experiments.runs.profiles.othello;

import org.joda.time.Duration;
import put.ci.cevo.experiments.othello.OthelloWPCInteraction;
import put.ci.cevo.experiments.profiles.generators.RSELStrategyGenerator;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
import put.ci.cevo.experiments.wpc.WPCPopulationFactory;
import put.ci.cevo.experiments.wpc.WPCUniformMutation;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.algorithms.stateful.StatefulEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.stateful.StatefulRSEL;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.NegativeFitnessAggregate;
import put.ci.cevo.framework.fitness.SimpleSumFitness;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.ExpectedUtility;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;

import java.io.File;
import java.util.Arrays;

public class OthelloPerfProfileDatabaseRSELGenerator implements Experiment {

	private final ThreadedContext context = new ThreadedContext();

	private final InteractionDomain<WPC, WPC> othello;
	private final PerformanceMeasure<WPC> expectedUtility;

	private final EvolutionModel<WPC> evolutionModel;

	private final PerfProfileDatabaseGenerator<WPC> perfProfileDatabaseGenerator;
	private final StatefulEvolutionaryAlgorithm<WPC> rselTowardsStrong;
	private final StatefulEvolutionaryAlgorithm<WPC> rselTowardsWeak;

	public OthelloPerfProfileDatabaseRSELGenerator() {
		// @formatter:off

		othello = new OthelloWPCInteraction(true);

		Integer numOpponents = 1000;
		expectedUtility = new ExpectedUtility<WPC, WPC>(
			othello,
			new WPCPopulationFactory(1.0, -1.0, OthelloBoard.NUM_FIELDS),
			numOpponents, context);


		evolutionModel = new MuPlusLambdaEvolutionModel<WPC>(
			25, 25,
			new WPCUniformMutation(-0.1, 0.1, 1.0));

		PopulationFactory<WPC> populationFactory = new WPCPopulationFactory(
			1.0, -1.0,
			OthelloBoard.NUM_FIELDS);

		PopulationFactory<WPC> individualsFactory = new WPCPopulationFactory(
			1.0, -1.0,
			OthelloBoard.NUM_FIELDS);

		final int populationSize = 50;
		final int sampleSize = 200;

		final Species<WPC> species = new Species<>(evolutionModel, populationFactory, populationSize);
		final Species<WPC> negativeSpecies = new Species<>(evolutionModel, populationFactory, populationSize);

		rselTowardsStrong =	new StatefulRSEL<WPC>(
			species,
			new RoundRobinInteractionScheme<>(othello),
			new SimpleSumFitness(),
			individualsFactory,
			sampleSize);

		rselTowardsWeak =	new StatefulRSEL<WPC>(
				negativeSpecies,
				new RoundRobinInteractionScheme<>(othello),
				new NegativeFitnessAggregate(new SimpleSumFitness()),
				individualsFactory,
				sampleSize);

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);
		final int maxBucketSize = 1000;
		final int numBuckets = 100;

		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<WPC>(
			expectedUtility,
			new RSELStrategyGenerator<WPC>(
				Arrays.asList(rselTowardsStrong, rselTowardsWeak)),
			numBuckets,
			maxBucketSize,
			new File("othello-bw-profile-db-rsel-symmetric.dump"),
			backupInterval, checkpointInterval,
			context);
		// @formatter:on
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}
}
