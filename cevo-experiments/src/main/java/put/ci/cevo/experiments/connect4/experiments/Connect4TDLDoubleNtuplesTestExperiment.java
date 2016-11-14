package put.ci.cevo.experiments.connect4.experiments;

import java.util.function.Function;

import put.ci.cevo.experiments.connect4.Connect4Interaction;
import put.ci.cevo.experiments.connect4.mappers.DoubleNTuplesConnect4AgentMapper;
import put.ci.cevo.experiments.connect4.mappers.DoubleNTuplesConnect4PlayerMapper;
import put.ci.cevo.experiments.connect4.measures.Connect4PerfectPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.measures.Connect4RandomPlayerPerformanceMeasure;
import put.ci.cevo.experiments.connect4.ntuples.Connect4NTuplesSystematicFactory;
import put.ci.cevo.experiments.ntuple.DoubleNTuplesFactory;
import put.ci.cevo.experiments.rl.AgentExpectedUtility;
import put.ci.cevo.framework.factories.IndividualFactory;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.games.OutputNegationActionValueFunction;
import put.ci.cevo.games.board.Board;
import put.ci.cevo.games.connect4.Connect4Action;
import put.ci.cevo.games.connect4.Connect4OpponentEnvironment;
import put.ci.cevo.games.connect4.Connect4SelfPlayEnvironment;
import put.ci.cevo.games.connect4.Connect4State;
import put.ci.cevo.games.connect4.players.Connect4DeltaNTuplesAgent;
import put.ci.cevo.games.connect4.players.Connect4Player;
import put.ci.cevo.games.connect4.players.HandConnect4Player;
import put.ci.cevo.games.connect4.players.RandomConnect4Player;
import put.ci.cevo.games.encodings.ntuple.DoubleNTupleAgent;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.encodings.ntuple.DoubleNTuplesStateValueFunction;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.rl.learn.TDAfterstateValueLearning;
import put.ci.cevo.rl.learn.TDAfterstateValueLearningV2;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.util.random.ThreadedContext;

public class Connect4TDLDoubleNtuplesTestExperiment implements Experiment {

	private static final int NUM_EPISODES = 10000000;

	private static Function<Double, Double> connect4RewardMapper = x -> (x + 1) / 2;

	private static final PerformanceMeasure<Agent<Connect4State, Connect4Action>> randomAgentMeasure =
			new AgentExpectedUtility<>(
					new Connect4OpponentEnvironment(new RandomConnect4Player(), Board.WHITE),
					10000, connect4RewardMapper);

	private static final PerformanceMeasure<Connect4Player> randomPlayerMeasure =
			new Connect4RandomPlayerPerformanceMeasure(new Connect4Interaction(true), 10000);

	private static final PerformanceMeasure<Connect4Player> handMeasure =
			new AgainstTeamPerformanceMeasure<>(new Connect4Interaction(false), new HandConnect4Player(), 10000);

	private static final PerformanceMeasure<Connect4Player> objectiveMeasure =
			new Connect4PerfectPlayerPerformanceMeasure(new Connect4Interaction(false), 100);

	private static final DoubleNTuplesConnect4AgentMapper agentMapper = new DoubleNTuplesConnect4AgentMapper(
			OutputNegationActionValueFunction::new);
	private static final DoubleNTuplesConnect4PlayerMapper playerMapper = new DoubleNTuplesConnect4PlayerMapper();

	@Override
	public void run(String[] args) {
		ThreadedContext context = new ThreadedContext(1, 1);
		IndividualFactory<DoubleNTuples> ntuplesFactory = new DoubleNTuplesFactory(new Connect4NTuplesSystematicFactory(
				0, 0, "1111|1111|1111"));
		//IndividualFactory<NTuples> ntuplesFactory = new NTuplesConnect4SnakeImprovedRandomIndividualFactory(70, 8, 0,	0);
		//IndividualFactory<NTuples> ntuplesFactory = new Connect4NTuplesSystematicFactory(0, 0, "1111; 1|1|1|1; 1000|0100|0010|0001; 11|11");

		DoubleNTuples nTuples = ntuplesFactory.createRandomIndividual(context.getRandomForThread());

		System.out.println(nTuples);
		System.out.println(nTuples.totalWeights());

		LearnableStateValueFunction<Connect4State> afterStateValueFunction = new DoubleNTuplesStateValueFunction<>(nTuples);
		Agent<Connect4State, Connect4Action> deltaAgent = new DoubleNTupleAgent<>(nTuples,
				t -> new Connect4DeltaNTuplesAgent(t, OutputNegationActionValueFunction::new));

		Environment<Connect4State, Connect4Action> learningEnvironment = new Connect4SelfPlayEnvironment();
		//Environment<Connect4State, Connect4Action> learningEnvironment = new Connect4OpponentEnvironment(deltaAgent);

		TDAfterstateValueLearning<Connect4State, Connect4Action> algorithm = new TDAfterstateValueLearning<>(
				learningEnvironment, afterStateValueFunction, deltaAgent);

		double eps = 1.0;
		final double learningRate = 0.001;
		for (int i = 0; i <= NUM_EPISODES; i++) {
			algorithm.fastLearningEpisode(eps, learningRate, context.getRandomForThread());
			if (i % 100000 == 0) {
				System.out.printf("eps: %.2f\n", eps);
				evalOnRand(nTuples, i, context);
				evalOnHand(nTuples, i, context);
				evalOnObj(nTuples, i, context);
				System.out.println();
				eps *= 0.8;
				if (eps < 0.1)
					eps = 0.1;
			}
		}
	}

	private static void evalOnRand(DoubleNTuples individual, int episode, ThreadedContext context) {
		{
			Agent<Connect4State, Connect4Action> player = agentMapper.getPhenotype(individual,
					context.getRandomForThread());
			double performance = randomAgentMeasure.measure(player, context).stats().getMean();
			System.out.println(String.format("black vs rand after %5d: %8.2f", episode, performance));
		}

		{
			Connect4Player player = playerMapper.getPhenotype(individual, context.getRandomForThread());
			double performance = randomPlayerMeasure.measure(player, context).stats().getMean();
			System.out.println(String.format(" both vs rand after %5d: %8.2f", episode, performance));
		}
	}

	private static void evalOnHand(DoubleNTuples individual, int episode, ThreadedContext context) {
		Connect4Player player = playerMapper.getPhenotype(individual, context.getRandomForThread());

		double performance = handMeasure.measure(player, context).stats().getMean();
		System.out.println(String.format("black vs hand after %5d: %8.2f", episode, performance));
	}

	private static void evalOnObj(DoubleNTuples individual, int episode, ThreadedContext context) {
		//TODO
		Connect4Player player = playerMapper.getPhenotype(individual, context.getRandomForThread());

		double performance = objectiveMeasure.measure(player, context).stats().getMean();
		System.out.println(String.format("black vs obj after %5d: %8.2f", episode, performance));
	}

	public static void main(String[] args) {
		new Connect4TDLDoubleNtuplesTestExperiment().run(args);
	}
}
