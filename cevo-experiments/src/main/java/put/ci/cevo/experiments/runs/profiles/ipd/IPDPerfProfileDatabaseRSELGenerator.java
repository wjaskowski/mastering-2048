package put.ci.cevo.experiments.runs.profiles.ipd;

import org.joda.time.Duration;
import put.ci.cevo.experiments.ipd.*;
import put.ci.cevo.experiments.profiles.generators.RSELStrategyGenerator;
import put.ci.cevo.experiments.runs.profiles.generic.PerfProfileDatabaseGenerator;
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
import put.ci.cevo.games.ipd.LinearPayoffInterpolator;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.random.ThreadedContext;

import java.io.File;
import java.util.Arrays;

public class IPDPerfProfileDatabaseRSELGenerator implements Experiment {

	private final ThreadedContext random = new ThreadedContext();

	private final InteractionDomain<IPDVector, IPDVector> ipd;
	private final PerformanceMeasure<IPDVector> expectedUtility;

	private final EvolutionModel<IPDVector> evolutionModel;

	private final PerfProfileDatabaseGenerator<IPDVector> perfProfileDatabaseGenerator;

	private final StatefulEvolutionaryAlgorithm<IPDVector> rselTowardsStrong;
	private final StatefulEvolutionaryAlgorithm<IPDVector> rselTowardsWeak;

	public IPDPerfProfileDatabaseRSELGenerator() {
		// @formatter:off
		int choices = 9;

		ipd = new IPDInteraction(new LinearPayoffInterpolator(choices), new IPDLookupTableMapper(), 150);

		Integer numOpponents = 1000;
		expectedUtility = new ExpectedUtility<>(
			ipd,
			new IPDPopulationFactory(choices),
			numOpponents, new ThreadedContext(123));


		evolutionModel = new MuPlusLambdaEvolutionModel<>(
			25, 25,
			new IPDMutation(0.2));

		PopulationFactory<IPDVector> populationFactory = new IPDPopulationFactory(choices);
		PopulationFactory<IPDVector> individualsFactory = new IPDPopulationFactory(choices);

		final int populationSize = 50;
		final int sampleSize = 200;

		final Species<IPDVector> species = new Species<>(evolutionModel, populationFactory, populationSize);
		final Species<IPDVector> negativeSpecies = new Species<>(evolutionModel, populationFactory, populationSize);

		rselTowardsStrong =	new StatefulRSEL<>(
			species,
			new RoundRobinInteractionScheme<>(ipd),
			new SimpleSumFitness(), 
			individualsFactory,
			sampleSize);

		rselTowardsWeak = new StatefulRSEL<>(
				negativeSpecies,
				new RoundRobinInteractionScheme<>(ipd),
				new NegativeFitnessAggregate(new SimpleSumFitness()),
				individualsFactory,
				sampleSize);

		final Duration backupInterval = Duration.standardHours(2);
		final Duration checkpointInterval = Duration.standardSeconds(60);
		final int maxBucketSize = 1000;
		final int numBuckets = 100;

		perfProfileDatabaseGenerator = new PerfProfileDatabaseGenerator<>(
			expectedUtility,
			new RSELStrategyGenerator<>(
				Arrays.asList(rselTowardsStrong, rselTowardsWeak)),
			numBuckets,
			maxBucketSize,
			new File("othello-bw-profile-db-rsel-symmetric.dump"),
			backupInterval, checkpointInterval,
			random);
		// @formatter:on
	}

	@Override
	public void run(String[] args) {
		perfProfileDatabaseGenerator.run();
	}

	public static void main(String[] args) {
		IPDPerfProfileDatabaseRSELGenerator exp = new IPDPerfProfileDatabaseRSELGenerator();
		exp.run(null);
	}
}
