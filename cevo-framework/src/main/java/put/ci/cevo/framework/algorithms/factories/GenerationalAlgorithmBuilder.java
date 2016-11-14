package put.ci.cevo.framework.algorithms.factories;

import static com.google.common.base.Objects.toStringHelper;

import java.util.List;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import put.ci.cevo.framework.algorithms.GenerationalOptimizationAlgorithm;
import put.ci.cevo.framework.algorithms.common.Species;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.fitness.FitnessAggregate;
import put.ci.cevo.framework.interactions.InteractionScheme;
import put.ci.cevo.framework.model.EvolutionModel;
import put.ci.cevo.framework.state.listeners.EvolutionStateListener;
import put.ci.cevo.util.BeanUtils;

public abstract class GenerationalAlgorithmBuilder<S, T, U extends GenerationalAlgorithmBuilder<S, T, U>> {

	protected FitnessAggregate aggregate;
	protected EvolutionModel<S> model;

	protected InteractionScheme<S, T> interactionScheme;
	protected PopulationFactory<S> factory;

	protected int populationSize;

	protected List<EvolutionStateListener> listeners;

	public GenerationalAlgorithmBuilder() {
		this.listeners = Lists.newArrayList();
	}

	public U addListener(EvolutionStateListener listener) {
		this.listeners.add(listener);
		return getBuilder();
	}

	public U removeListener(EvolutionStateListener listener) {
		this.listeners.remove(listener);
		return getBuilder();
	}

	public List<EvolutionStateListener> getListeners() {
		return listeners;
	}

	public U setListeners(List<EvolutionStateListener> listeners) {
		this.listeners = listeners;
		return getBuilder();
	}

	public FitnessAggregate getFitnessAggregate() {
		return aggregate;
	}

	public U setFitnessAggregate(FitnessAggregate aggregate) {
		this.aggregate = aggregate;
		return getBuilder();
	}

	public EvolutionModel<S> getEvolutionModel() {
		return model;
	}

	public U setEvolutionModel(EvolutionModel<S> model) {
		this.model = model;
		return getBuilder();
	}

	public InteractionScheme<S, T> getInteractionScheme() {
		return interactionScheme;
	}

	public U setInteractionScheme(InteractionScheme<S, T> interactionScheme) {
		this.interactionScheme = interactionScheme;
		return getBuilder();
	}

	public PopulationFactory<S> getPopulationFactory() {
		return factory;
	}

	public U setPopulationFactory(PopulationFactory<S> factory) {
		this.factory = factory;
		return getBuilder();

	}

	public int getPopulationSize() {
		return populationSize;
	}

	public U setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
		return getBuilder();
	}

	protected Species<S> createSpecies() {
		speciesNullCheck();
		return new Species<>(model, factory, populationSize);
	}

	public U fromTemplate(GenerationalAlgorithmBuilder<S, T, U> builder) {
		BeanUtils.copyProperties(builder, this);
		return getBuilder();
	}

	public GenerationalOptimizationAlgorithm buildWithListeners() {
		GenerationalOptimizationAlgorithm algorithm = build();
		for (EvolutionStateListener listener : listeners) {
			algorithm.addNextGenerationListener(listener);
		}
		return algorithm;
	}

	public abstract GenerationalOptimizationAlgorithm build();

	protected abstract U getBuilder();

	@Override
	public String toString() {
		return toStringHelper(this).omitNullValues().add("aggregate", aggregate).add("model", model)
			.add("scheme", interactionScheme).add("factory", factory).add("populationSize", populationSize).toString();
	}

	protected void nullchecks() {
		speciesNullCheck();
		if (interactionScheme == null) {
			throw new IllegalStateException("Interaction scheme cannot be null!");
		}
	}

	private void speciesNullCheck() {
		if (aggregate == null) {
			throw new IllegalStateException("Aggregate cannot be null!");
		}
		if (model == null) {
			throw new IllegalStateException("Model cannot be null!");
		}
		if (factory == null) {
			throw new IllegalStateException("Factory cannot be null!");
		}
		if (populationSize < 1) {
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
		GenerationalAlgorithmBuilder<S, T, U> other = (GenerationalAlgorithmBuilder<S, T, U>) obj;
		return new EqualsBuilder().append(aggregate, other.aggregate).append(model, other.model)
			.append(interactionScheme, other.interactionScheme).append(factory, other.factory)
			.append(populationSize, other.populationSize).append(listeners, other.listeners).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(aggregate).append(model).append(interactionScheme).append(factory)
			.append(populationSize).append(listeners).toHashCode();
	}

}
