package put.ci.cevo.experiments.benchmarks;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.LogManager;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.experiments.connect4.Connect4AgentInteraction;
import put.ci.cevo.experiments.connect4.Connect4Interaction;
import put.ci.cevo.experiments.connect4.mappers.NTuplesConnect4ThillAgentMapper;
import put.ci.cevo.experiments.connect4.mappers.NTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.ntuples.Connect4AllStraightFactory;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplePopulationFactory;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.games.connect4.players.Connect4PerfectPlayer;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.players.RandomConnect4Player;
import put.ci.cevo.games.connect4.thill.c4.Agent;
import put.ci.cevo.games.connect4.thill.c4.AlphaBetaAgent;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.log4j.Level.INFO;
import static put.ci.cevo.games.connect4.players.Connect4PerfectPlayer.Randomization.RANDOMIZE_LOSSES_AND_MOVES;

public class Connect4Benchmark extends AbstractBenchmark {

	private static final int POP_SIZE = 100;
	private static final int TUPLE_SIZE = 4;

	private static final double MIN_WEIGHT = -0.1;
	private static final double MAX_WEIGHT = 0.1;

	private ThreadedContext context;

	@Before
	public void setUp() {
		LogManager.getRootLogger().setLevel(INFO);
		context = new ThreadedContext(123);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testConnect4PlayerInteractionPerformance() {
		RandomDataGenerator random = context.getRandomForThread();
		Connect4Interaction c4 = new Connect4Interaction();

		NTuplesConnect4PlayerMapper mapper = new NTuplesConnect4PlayerMapper();
		Connect4NTuplePopulationFactory factory = new Connect4NTuplePopulationFactory(TUPLE_SIZE, MIN_WEIGHT, MAX_WEIGHT);
		List<Connect4Player> population = factory.createPopulation(POP_SIZE, random)
				.stream().map(ntuples -> mapper.getPhenotype(ntuples, random)).collect(toList());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (Connect4Player s : population) {
			for (Connect4Player t : population) {
				InteractionResult.aggregate(res, c4.interact(s, t, random));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testConnect4AgentInteractionPerformance() {
		RandomDataGenerator random = context.getRandomForThread();
		Connect4AgentInteraction c4 = new Connect4AgentInteraction();

		NTuplesConnect4ThillAgentMapper mapper = new NTuplesConnect4ThillAgentMapper();
		Connect4NTuplePopulationFactory factory = new Connect4NTuplePopulationFactory(TUPLE_SIZE, MIN_WEIGHT, MAX_WEIGHT);
		List<Agent> population = factory.createPopulation(POP_SIZE, random)
				.stream().map(ntuples -> mapper.getPhenotype(ntuples, random)).collect(toList());

		InteractionResult res = new InteractionResult(0, 0, 0);
		for (Agent s : population) {
			for (Agent t : population) {
				InteractionResult.aggregate(res, c4.interact(s, t, random));
			}
		}
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testPerfectPlayerInteraction() {
		RandomDataGenerator random = context.getRandomForThread();
		Connect4Interaction c4 = new Connect4Interaction();

		NTuplesConnect4PlayerMapper mapper = new NTuplesConnect4PlayerMapper();
		Connect4AllStraightFactory factory = new Connect4AllStraightFactory(TUPLE_SIZE, MIN_WEIGHT, MAX_WEIGHT);
		NTuples randomIndividual = factory.createRandomIndividual(random);

		c4.interact(mapper.getPhenotype(randomIndividual, random),
				new Connect4PerfectPlayer(RANDOMIZE_LOSSES_AND_MOVES), random);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testPerfectAgentInteraction() {
		RandomDataGenerator random = context.getRandomForThread();
		Connect4AgentInteraction c4 = new Connect4AgentInteraction();

		NTuplesConnect4ThillAgentMapper mapper = new NTuplesConnect4ThillAgentMapper();
		Connect4AllStraightFactory factory = new Connect4AllStraightFactory(TUPLE_SIZE, MIN_WEIGHT, MAX_WEIGHT);
		NTuples randomIndividual = factory.createRandomIndividual(random);

		c4.interact(mapper.getPhenotype(randomIndividual, random),
				AlphaBetaAgent.createAgent(RANDOMIZE_LOSSES_AND_MOVES), random);
	}

	@BenchmarkOptions(benchmarkRounds = 5, warmupRounds = 1)
	@Test
	public void testRandomPlayerInteraction() {
		RandomDataGenerator random = context.getRandomForThread();
		Connect4Interaction c4 = new Connect4Interaction();

		NTuplesConnect4PlayerMapper mapper = new NTuplesConnect4PlayerMapper();
		Connect4AllStraightFactory factory = new Connect4AllStraightFactory(TUPLE_SIZE, MIN_WEIGHT, MAX_WEIGHT);
		NTuples randomIndividual = factory.createRandomIndividual(random);

		c4.interact(mapper.getPhenotype(randomIndividual, random), new RandomConnect4Player(), random);
	}

}
