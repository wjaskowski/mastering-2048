package put.ci.cevo.experiments.numbers;

import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.measures.Measurement;
import put.ci.cevo.framework.measures.PerformanceMeasure;
import put.ci.cevo.util.RandomFactory;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.vectors.DoubleVector;

public class MinDimensionProgressMeasure implements PerformanceMeasure<DoubleVector> {
	@Override
	public Measurement measure(RandomFactory<DoubleVector> subjectFactory, ThreadedContext context) {
		Measurement.Builder builder = new Measurement.Builder();
		builder.add(new InteractionResult(subjectFactory.create(context).min(), 0, 1));
		return builder.build();
	}
}
