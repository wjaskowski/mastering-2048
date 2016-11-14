package put.ci.cevo.experiments.shaping;

import static put.ci.cevo.framework.evaluators.EvaluatorUtils.assignFitness;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.rl.OthelloWPCStateInteraction;
import put.ci.cevo.framework.algorithms.OnePopulationEvolutionaryAlgorithm;
import put.ci.cevo.framework.algorithms.RetrospectiveAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.evaluators.PopulationEvaluator;
import put.ci.cevo.framework.factories.StaticPopulationFactory;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.interactions.InteractionTable;
import put.ci.cevo.framework.interactions.RoundRobinInteractionScheme;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.queries.BestOfLastGenerationSolutionQuery;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.framework.termination.GenerationsTarget;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.rl.agent.functions.wpc.WPC;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class EvolvabilityInteractionDomain implements InteractionDomain<List<OthelloState>, List<WPC>> {

	private static final int THREADS = Runtime.getRuntime().availableProcessors();

	private final int generations;
	private final FitnessAggregate aggregate;
	private final EvolutionModel<WPC> evolutionModel;
	private final PerformanceMeasure<WPC> performanceMeasure;

	@AccessedViaReflection
	public EvolvabilityInteractionDomain(FitnessAggregate aggregate, EvolutionModel<WPC> evolutionModel,
			PerformanceMeasure<WPC> performanceMeasure, int generations) {
		this.aggregate = aggregate;
		this.evolutionModel = evolutionModel;
		this.performanceMeasure = performanceMeasure;
		this.generations = generations;
	}

	@Override
	public InteractionResult interact(final List<OthelloState> candidate, List<WPC> opponent, RandomDataGenerator random) {
		Species<WPC> species = new Species<>(evolutionModel, new StaticPopulationFactory<>(opponent), opponent.size());
		final RoundRobinInteractionScheme<WPC, OthelloState> scheme = new RoundRobinInteractionScheme<>(
			new OthelloWPCStateInteraction());

		OnePopulationEvolutionaryAlgorithm<WPC> algorithm = new OnePopulationEvolutionaryAlgorithm<>(
			species, new PopulationEvaluator<WPC>() {
				@Override
				public EvaluatedPopulation<WPC> evaluate(List<WPC> population,
						int generation,
						ThreadedContext context) {
					InteractionTable<WPC, OthelloState> table = scheme.interact(population, candidate, context);
					Map<WPC, Fitness> fitness = aggregate.aggregateFitness(table.getSolutionsPayoffs(), context);
					return new EvaluatedPopulation<>(assignFitness(population, generation, table.getEfforts(), fitness));
				}
			});

		ThreadedContext context = new ThreadedContext(new ThreadedRandom(random.getRandomGenerator()), THREADS);
		RetrospectiveAlgorithm retro = new RetrospectiveAlgorithm(algorithm);
		Retrospector retrospector = retro.evolve(new GenerationsTarget(generations), context);
		List<EvaluatedIndividual<WPC>> result = retrospector.inquire(new BestOfLastGenerationSolutionQuery<WPC>(),
			performanceMeasure, context).toList();
		double fitness = result.get(0).getFitness();

		return new InteractionResult(fitness, -fitness, generations);
	}
}
