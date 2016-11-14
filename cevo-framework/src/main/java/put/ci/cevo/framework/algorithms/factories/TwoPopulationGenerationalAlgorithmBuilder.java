package put.ci.cevo.framework.algorithms.factories;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.evaluators.TwoPopulationEvaluator;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.framework.state.listeners.LastGenerationListener;
import put.ci.cevo.util.BeanUtils;

import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public abstract class TwoPopulationGenerationalAlgorithmBuilder<S, T, U extends TwoPopulationGenerationalAlgorithmBuilder<S, T, U>> {

	protected FitnessAggregate solutionsAggregate;
	protected EvolutionModel<S> solutionsModel;
	protected PopulationFactory<S> solutionsFactory;
	protected int solutionsPopulationSize;

	protected FitnessAggregate testsAggregate;
	protected EvolutionModel<T> testsModel;
	protected PopulationFactory<T> testsFactory;
	protected int testsPopulationSize;

	protected InteractionScheme<S, T> interactionScheme;
	protected TwoPopulationEvaluator<S, T> evaluator;

	protected List<EvolutionStateListener> nextGenerationListeners;
	protected List<LastGenerationListener> lastGenerationListeners;

	public TwoPopulationGenerationalAlgorithmBuilder() {
		this.nextGenerationListeners = Lists.newArrayList();
	}

	public U addNextGenerationListener(EvolutionStateListener listener) {
		this.nextGenerationListeners.add(listener);
		return getBuilder();
	}

	public U removeNextGenerationListener(EvolutionStateListener listener) {
		this.nextGenerationListeners.remove(listener);
		return getBuilder();
	}

	public List<EvolutionStateListener> getNextGenerationListeners() {
		return nextGenerationListeners;
	}

	public U setNextGenerationListeners(List<EvolutionStateListener> listeners) {
		this.nextGenerationListeners = listeners;
		return getBuilder();
	}

	public U addLastGenerationListener(LastGenerationListener listener) {
		this.lastGenerationListeners.add(listener);
		return getBuilder();
	}

	public U removeLastGenerationListener(LastGenerationListener listener) {
		this.lastGenerationListeners.remove(listener);
		return getBuilder();
	}

	public List<LastGenerationListener> getLastGenerationListeners() {
		return lastGenerationListeners;
	}

	public U setLastGenerationListeners(List<LastGenerationListener> listeners) {
		this.lastGenerationListeners = listeners;
		return getBuilder();
	}

	public FitnessAggregate getSolutionsFitnessAggregate() {
		return solutionsAggregate;
	}

	public U setSolutionsFitnessAggregate(FitnessAggregate aggregate) {
		this.solutionsAggregate = aggregate;
		return getBuilder();
	}

	public FitnessAggregate getTestsFitnessAggregate() {
		return testsAggregate;
	}

	public U setTestsFitnessAggregate(FitnessAggregate aggregate) {
		this.testsAggregate = aggregate;
		return getBuilder();
	}

	public EvolutionModel<S> getSolutionsEvolutionModel() {
		return solutionsModel;
	}

	public U setSolutionsEvolutionModel(EvolutionModel<S> model) {
		this.solutionsModel = model;
		return getBuilder();
	}

	public EvolutionModel<T> getTestsEvolutionModel() {
		return testsModel;
	}

	public U setTestsEvolutionModel(EvolutionModel<T> model) {
		this.testsModel = model;
		return getBuilder();
	}

	public InteractionScheme<S, T> getInteractionScheme() {
		return interactionScheme;
	}

	public U setInteractionScheme(InteractionScheme<S, T> interactionScheme) {
		this.interactionScheme = interactionScheme;
		return getBuilder();
	}

	public PopulationFactory<S> getSolutionsPopulationFactory() {
		return solutionsFactory;
	}

	public U setSolutionsPopulationFactory(PopulationFactory<S> factory) {
		this.solutionsFactory = factory;
		return getBuilder();

	}

	public PopulationFactory<T> getTestsPopulationFactory() {
		return testsFactory;
	}

	public U setTestsPopulationFactory(PopulationFactory<T> factory) {
		this.testsFactory = factory;
		return getBuilder();

	}

	public int getSolutionsPopulationSize() {
		return solutionsPopulationSize;
	}

	public U setSolutionsPopulationSize(int populationSize) {
		this.solutionsPopulationSize = populationSize;
		return getBuilder();
	}

	public int getTestsPopulationSize() {
		return solutionsPopulationSize;
	}

	public U setTestsPopulationSize(int testsPopulationSize) {
		this.testsPopulationSize = testsPopulationSize;
		return getBuilder();
	}

	protected Species<S> createSolutionsSpecies() {
		solutionsSpeciesNullCheck();
		return new Species<>(solutionsModel, solutionsFactory, solutionsPopulationSize);
	}

	protected Species<T> createTestsSpecies() {
		testsSpeciesNullCheck();
		return new Species<>(testsModel, testsFactory, testsPopulationSize);
	}

	public U fromTemplate(TwoPopulationGenerationalAlgorithmBuilder<S, T, ?> builder) {
		BeanUtils.copyProperties(builder, this);
		return getBuilder();
	}

	public GenerationalOptimizationAlgorithm buildWithListeners() {
		GenerationalOptimizationAlgorithm algorithm = build();
		for (EvolutionStateListener listener : nextGenerationListeners) {
			algorithm.addNextGenerationListener(listener);
		}
		return algorithm;
	}

	public abstract GenerationalOptimizationAlgorithm build();

	protected abstract U getBuilder();

	@Override
	public String toString() {
		return toStringHelper(this).omitNullValues().add("aggregate", solutionsAggregate).add("model", solutionsModel)
			.add("scheme", interactionScheme).add("factory", solutionsFactory)
			.add("populationSize", solutionsPopulationSize).toString();
	}

	protected void nullchecks() {
		solutionsSpeciesNullCheck();
		if (interactionScheme == null) {
			throw new IllegalStateException("Interaction scheme cannot be null!");
		}
	}

	private void solutionsSpeciesNullCheck() {
		if (solutionsModel == null) {
			throw new IllegalStateException("Model cannot be null!");
		}
		if (solutionsFactory == null) {
			throw new IllegalStateException("Factory cannot be null!");
		}
		if (solutionsPopulationSize < 1) {
			throw new IllegalStateException("Population size must be greater than zero!");
		}
	}

	private void testsSpeciesNullCheck() {
		if (testsModel == null) {
			throw new IllegalStateException("Model cannot be null!");
		}
		if (testsFactory == null) {
			throw new IllegalStateException("Factory cannot be null!");
		}
		if (testsPopulationSize < 1) {
			throw new IllegalStateException("Population size must be greater than zero!");
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		TwoPopulationGenerationalAlgorithmBuilder<S, T, U> other = (TwoPopulationGenerationalAlgorithmBuilder<S, T, U>) obj;
		return new EqualsBuilder().append(solutionsAggregate, other.solutionsAggregate)
			.append(solutionsModel, other.solutionsModel).append(interactionScheme, other.interactionScheme)
			.append(solutionsFactory, other.solutionsFactory)
			.append(solutionsPopulationSize, other.solutionsPopulationSize)
			.append(nextGenerationListeners, other.nextGenerationListeners).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(solutionsAggregate).append(solutionsModel).append(interactionScheme)
			.append(solutionsFactory).append(solutionsPopulationSize).append(nextGenerationListeners).toHashCode();
	}
}
