package put.ci.cevo.experiments.benchmarks;

import java.util.List;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.experiments.mlp.MLPIndividualFactory;
import put.ci.cevo.experiments.ntuple.NTuplesRandomIndividualFactory;
import put.ci.cevo.experiments.ntuple.OthelloNTuplesAllStraightFactory;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.othello.OthelloWPCIndividualFactory;
import put.ci.cevo.experiments.rl.*;
import put.ci.cevo.experiments.wpc.othello.mappers.MLPOthelloPlayerMapper;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.factories.UniformRandomPopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.OthelloNTupleRandomFactory;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloSelfPlayEnvironment;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.rl.agent.functions.RealFunction;
import put.ci.cevo.rl.agent.functions.mlp.MLP;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.random.ThreadedRandom;

public class OthelloBenchmark extends AbstractBenchmark {

	private ThreadedRandom random;

	@Before
	public void setUp() {
		random = new ThreadedRandom(new MersenneTwister(123));
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testNTuplesAllPerformance() {
		final int POP_SIZE = 50;
		final OthelloInteractionDomain domain = new OthelloInteractionDomain(new DoubleOthello());
		PopulationFactory<NTuples> generator = new UniformRandomPopulationFactory<>(
			new OthelloNTuplesAllStraightFactory(2, -0.1, 0.1));
		final List<NTuples> population = generator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (NTuples s : population) {
			for (NTuples t : population) {
				InteractionResult.aggregate(res, domain.interact(new OthelloNTuplesPlayer(s), new OthelloNTuplesPlayer(
						t), random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testNTuplesPerformance() {
		final int POP_SIZE = 50;
		final OthelloInteractionDomain domain = new OthelloInteractionDomain(new DoubleOthello());
		PopulationFactory<NTuples> generator = new UniformRandomPopulationFactory<>(
			new NTuplesRandomIndividualFactory(
					9, new OthelloNTupleRandomFactory(5, -1.0, 1.0), new RotationMirrorSymmetryExpander(
					OthelloBoard.SIZE)));
		final List<NTuples> population = generator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (NTuples s : population) {
			for (NTuples t : population) {
				InteractionResult.aggregate(res, domain.interact(new OthelloNTuplesPlayer(s), new OthelloNTuplesPlayer(
						t), random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testNTuplesSmallPerformance() {
		final int POP_SIZE = 50;
		final OthelloInteractionDomain OTHELLO = new OthelloInteractionDomain(new DoubleOthello());
		PopulationFactory<NTuples> generator = new UniformRandomPopulationFactory<>(
			new OthelloNTuplesAllStraightFactory(2, -0.1, 0.1));
		final List<NTuples> population = generator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (NTuples s : population) {
			for (NTuples t : population) {
				InteractionResult.aggregate(res, OTHELLO.interact(new OthelloNTuplesPlayer(s), new OthelloNTuplesPlayer(
						t), random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testWPCPerformance() {
		final int POP_SIZE = 50;
		final OthelloInteractionDomain domain = new OthelloInteractionDomain(new DoubleOthello());

		PopulationFactory<WPC> generator = new UniformRandomPopulationFactory<>(new OthelloWPCIndividualFactory());
		List<WPC> population = generator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (WPC s : population) {
			for (WPC t : population) {
				InteractionResult.aggregate(res, domain.interact(new OthelloWPCPlayer(s), new OthelloWPCPlayer(t),
						random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testMLPvsWPCPerformance() {
		final int POP_SIZE = 50;
		final OthelloInteractionDomain othello = new OthelloInteractionDomain(new DoubleOthello());

		PopulationFactory<MLP> mlpGenerator = new UniformRandomPopulationFactory<>(new MLPIndividualFactory(
			64, 10, 1, -1.0, 1.0));
		List<MLP> mlpPopulation = mlpGenerator.createPopulation(POP_SIZE, random.forThread());

		PopulationFactory<WPC> wpcGenerator = new UniformRandomPopulationFactory<>(new OthelloWPCIndividualFactory());
		List<WPC> wpcPopulation = wpcGenerator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (MLP s : mlpPopulation) {
			for (WPC t : wpcPopulation) {
				OthelloPlayer mlpPlayer = new MLPOthelloPlayerMapper().getPhenotype(s, random.forThread());
				OthelloWPCPlayer wpcPlayer = new OthelloWPCPlayer(t);
				InteractionResult.aggregate(res, othello.interact(mlpPlayer, wpcPlayer, random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testMDPInteractionsWPCPerformance() {
		final int POP_SIZE = 50;
		OthelloSelfPlayEnvironment environment = new OthelloSelfPlayEnvironment();
		final InteractionDomain<RealFunction, WPC> othello = new MDPGenotypeMappingInteraction<>(
				new RealFunctionGameAgentMapping<>(environment),
				new WPCOpponentEnvironmentMapping<>(environment),
				new MDPEpisodeInteraction<>(new OthelloInteractionEvaluator()));

		PopulationFactory<WPC> generator = new UniformRandomPopulationFactory<>(new OthelloWPCIndividualFactory());
		List<WPC> population = generator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (WPC s : population) {
			for (WPC t : population) {
				InteractionResult.aggregate(res, othello.interact(s, t, random.forThread()));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testMDPInteractionsMLPPerformance() {
		final int POP_SIZE = 50;
		OthelloSelfPlayEnvironment environment = new OthelloSelfPlayEnvironment();
		final InteractionDomain<RealFunction, WPC> othello = new MDPGenotypeMappingInteraction<>(
				new RealFunctionGameAgentMapping<>(environment),
				new WPCOpponentEnvironmentMapping<>(environment),
				new MDPEpisodeInteraction<>(new OthelloInteractionEvaluator()));

		PopulationFactory<MLP> mlpGenerator = new UniformRandomPopulationFactory<>(new MLPIndividualFactory(
			64, 10, 1, -1.0, 1.0));
		List<MLP> mlpPopulation = mlpGenerator.createPopulation(POP_SIZE, random.forThread());

		PopulationFactory<WPC> wpcGenerator = new UniformRandomPopulationFactory<>(new OthelloWPCIndividualFactory());
		List<WPC> wpcPopulation = wpcGenerator.createPopulation(POP_SIZE, random.forThread());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (MLP s : mlpPopulation) {
			for (WPC t : wpcPopulation) {
				InteractionResult.aggregate(res, othello.interact(s, t, random.forThread()));
			}
		}
	}
}
