package put.ci.cevo.framework.state;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.ScalarFitness;
import put.ci.cevo.util.TypeUtils;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.io.Serializable;

import static com.google.common.base.Objects.toStringHelper;

public class EvaluatedIndividual<T> implements Comparable<EvaluatedIndividual<T>>, Serializable {

	private static final long serialVersionUID = 201303191025L;

	private final T individual;
	private final Fitness fitness;

	private final long effort;
	private final int generation; //TODO: This should be removed. It is not a logical part of EvaluatedIndividual

	public EvaluatedIndividual(T individual, double fitness) {
		this(individual, fitness, -1, -1);
	}

	public EvaluatedIndividual(T individual, double fitness, long effort) {
		this(individual, fitness, -1, effort);
	}

	public EvaluatedIndividual(T individual, double fitness, int generation, long effort) {
		this(individual, new ScalarFitness(fitness), generation, effort);
	}

	public EvaluatedIndividual(T individual, Fitness fitness) {
		this(individual, fitness, -1, -1);
	}

	public EvaluatedIndividual(T individual, Fitness fitness, int generation, long effort) {
		this.individual = individual;
		this.fitness = fitness;
		this.generation = generation;
		this.effort = effort;
	}

	public T getIndividual() {
		return individual;
	}

	public double getFitness() {
		return fitness.fitness();
	}

	public Fitness fitness() {
		return fitness;
	}

	public <F> F fitness(Class<F> clazz) {
		return TypeUtils.explicitCast(fitness);
	}

	public int getGeneration() {
		return generation;
	}

	public long getEffort() {
		return effort;
	}

	@Override
	public int compareTo(EvaluatedIndividual<T> evaluatedIndividual) {
		return fitness.compareTo(evaluatedIndividual.fitness);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		final EvaluatedIndividual<?> that = (EvaluatedIndividual<?>) o;
		return new EqualsBuilder().append(fitness, that.fitness).append(effort, that.effort)
			.append(generation, that.generation).append(individual, that.individual).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fitness).hashCode();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("Fitness", fitness).add("Generation", generation).toString();
	}

	public EvaluatedIndividual<T> withObjectiveFitness(double fitness) {
		return new EvaluatedIndividual<T>(individual, fitness, generation, effort);
	}

	public static <T> EvaluatedIndividual<T> template(T individual, EvaluatedIndividual<?> other) {
		return new EvaluatedIndividual<>(individual, other.getFitness(), other.getGeneration(), other.getEffort());
	}

	public static <T> Transform<EvaluatedIndividual<T>, Long> toEffort() {
		return new Transform<EvaluatedIndividual<T>, Long>() {
			@Override
			public Long transform(EvaluatedIndividual<T> object) {
				return object.getEffort();
			}
		};
	}

	public static <T> Transform<EvaluatedIndividual<T>, T> toIndividual() {
		return new Transform<EvaluatedIndividual<T>, T>() {
			@Override
			public T transform(EvaluatedIndividual<T> object) {
				return object.getIndividual();
			}
		};
	}

	public static <T> Transform<EvaluatedIndividual<T>, Double> toFitness() {
		return new Transform<EvaluatedIndividual<T>, Double>() {
			@Override
			public Double transform(EvaluatedIndividual<T> individual) {
				return individual.getFitness();
			}
		};
	}
}
