package put.ci.cevo.experiments.runs.othello;

import static java.lang.Runtime.getRuntime;

import java.io.File;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.wpc.WPCGaussianMutation;
import put.ci.cevo.experiments.wpc.WPCIndividualFactory;
import put.ci.cevo.experiments.wpc.WPCPopulationFactory;
import put.ci.cevo.experiments.wpc.othello.mappers.WPCOthelloPlayerMapper;
import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.PerformanceMeasureIndividualEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.measures.AgainstTeamPerformanceMeasure;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.model.MuPlusLambdaEvolutionModel;
import put.ci.cevo.framework.operators.mutation.MutationOperator;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.games.othello.players.OthelloWPCPlayer;
import put.ci.cevo.games.othello.players.published.OthelloStandardWPCHeuristicPlayer;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class WPCWinnerOfOthelloLeagueExperiment implements Experiment, EvolutionStateListener {

	private static final int THREADS = getRuntime().availableProcessors();
	private static final int NUM_INTERACTIONS_FOR_PERFORMANCE_MEASURE = 50000;
	private static final int POPULATION_SIZE = 200;
	private static final int MU = 50;
	private static final int LAMBDA = POPULATION_SIZE - MU;
	private static final double FORCE_RANDOM_MOVE_PROBABILITY = 0.1;
	private static final int NUM_INTERACTIONS_FOR_FITNESS = 2500;

	private EvaluatedIndividual<WPC> bestOfRun = new EvaluatedIndividual<>(null, 0);

	private final ThreadedContext context = new ThreadedContext(1234, THREADS);
	Logger logger = Logger.getLogger(WPCWinnerOfOthelloLeagueExperiment.class);

	// Interaction environment
	final InteractionDomain<OthelloPlayer, OthelloPlayer> othello =
		new OthelloInteractionDomain(new DoubleOthello(FORCE_RANDOM_MOVE_PROBABILITY));

	final WPCOthelloPlayerMapper genotypePhenotypeMapper = new WPCOthelloPlayerMapper();

	// Fitness is defined as average interaction result against Standard WPC Heuristic
	final PerformanceMeasure<OthelloPlayer> fitnessMeasure =
			new AgainstTeamPerformanceMeasure<>(
			othello,
			new OthelloStandardWPCHeuristicPlayer().create(),
			NUM_INTERACTIONS_FOR_FITNESS
		);

	// Objective external performance measure is the same as fitness but more interactions
	final PerformanceMeasure<OthelloPlayer> externalPerformanceMeasure =
		new AgainstTeamPerformanceMeasure<>(
			othello,
			new OthelloStandardWPCHeuristicPlayer().create(),
			NUM_INTERACTIONS_FOR_PERFORMANCE_MEASURE
		);

	@Override
	public void run(String[] args) {
		Logger.getRootLogger().setLevel(Level.INFO);

		MutationOperator<WPC> gaussianMutation = new WPCGaussianMutation(0.1, 0.1, 0);

		EvolutionModel<WPC> muPlusLambda = new MuPlusLambdaEvolutionModel<>(MU, LAMBDA, gaussianMutation);
		PopulationFactory<WPC> initialPopulationFactory = new WPCPopulationFactory(
			new WPCIndividualFactory(OthelloBoard.NUM_FIELDS, -0.1, 0.1));
		Species<WPC> species = new Species<>(muPlusLambda, initialPopulationFactory, POPULATION_SIZE);

		OnePopulationEvolutionaryAlgorithm<WPC> rsel = new OnePopulationEvolutionaryAlgorithm<>(
			species,
			new PerformanceMeasureIndividualEvaluator<>(
				genotypePhenotypeMapper,
				fitnessMeasure
			)
		);

		rsel.addNextGenerationListener(this);

		// Ad infinitum
		rsel.evolve(new GenerationsTarget(Integer.MAX_VALUE), context);
	}

	@Override
	public void onNextGeneration(EvolutionState state) {
		EvaluatedIndividual<WPC> bestOfGeneration = state.getBestSolution();
		if (bestOfGeneration.getFitness() > bestOfRun.getFitness()) {
			OthelloWPCPlayer player = genotypePhenotypeMapper.getPhenotype(bestOfGeneration.getIndividual(), context.getRandomForThread());
			double objectiveFitness = externalPerformanceMeasure.measure(player, context).stats().getMean();
			if (objectiveFitness > bestOfRun.getFitness()) {
				bestOfRun = new EvaluatedIndividual<>(bestOfGeneration.getIndividual(), objectiveFitness);

				// Save the best
				try {
					File file = new File(String.format("best_wpc_%04d.dump", state.getGeneration()));
					SerializationManagerFactory.create().serialize(bestOfRun, file);
				} catch (SerializationException e) {
					throw new RuntimeException(e.getMessage(), e);
				}

				logger.info("\n" + OthelloWPCPlayer.createMarginWPC(bestOfRun.getIndividual()));
			}
		}

		SummaryStatistics stats = new SummaryStatistics();
		for (EvaluatedIndividual<WPC> ind : state.<WPC>getEvaluatedSolutions()) {
			stats.addValue(ind.getFitness());
		}
		logger.info(
				String.format("gen = %3d, eff = %10d, pop = (%.3f,%.3f,%.3f), obj_best = %.5f", state.getGeneration(),
						state.getTotalEffort(), stats.getMin(), stats.getMean(), stats.getMax(),
						bestOfRun.getFitness()));
	}

	public WPCWinnerOfOthelloLeagueExperiment() {
	}

	public static void main(String[] args) {
		new WPCWinnerOfOthelloLeagueExperiment().run(args);
	}
}
