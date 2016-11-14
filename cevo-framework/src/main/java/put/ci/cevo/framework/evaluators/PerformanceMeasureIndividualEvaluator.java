package put.ci.cevo.framework.evaluators;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.random.ThreadedContext;

/**
 * Evaluates an individual using a given performance measure, which operates on Phenotypes
 */
public class PerformanceMeasureIndividualEvaluator<S, X> implements IndividualEvaluator<S> {

	private final PerformanceMeasure<X> measure;
	private final GenotypePhenotypeMapper<S, X> mapper;

	public PerformanceMeasureIndividualEvaluator(GenotypePhenotypeMapper<S, X> mapper, PerformanceMeasure<X> measure) {
		this.measure = measure;
		this.mapper = mapper;
	}

	@Override
	public EvaluatedIndividual<S> evaluate(S individual, ThreadedContext context) {
		Measurement measurement = measure.measure(mapper.getPhenotype(individual, context.getRandomForThread()), context);
		return new EvaluatedIndividual<>(individual, measurement.stats().getMean(), measurement.getEffort());
	}
}
