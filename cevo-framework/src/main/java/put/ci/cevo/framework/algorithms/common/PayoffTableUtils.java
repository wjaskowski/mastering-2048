package put.ci.cevo.framework.algorithms.common;

import com.google.common.primitives.Doubles;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;
import put.ci.cevo.framework.algorithms.common.PayoffTable.PayoffTableBuilder;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

import java.util.List;
import java.util.Map;

import static java.util.Collections.max;
import static java.util.Collections.min;
import static org.apache.commons.math3.stat.StatUtils.mean;
import static org.apache.commons.math3.stat.StatUtils.variance;
import static org.apache.commons.math3.util.FastMath.sqrt;
import static org.ejml.ops.CommonOps.identity;
import static org.ejml.ops.CommonOps.scale;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.doubleAdd;

public class PayoffTableUtils {

	public static <S, T> PayoffTable<S, T> applyFitnessSharing(final PayoffTable<S, T> payoff) {
		final Map<T, Double> testsSum = seq(payoff.tests()).keysToMap(new Transform<T, Double>() {
			@Override
			public Double transform(T test) {
				return payoff.testPayoffs(test).aggregate(doubleAdd());
			}
		});
		PayoffTableBuilder<S, T> builder = PayoffTable.create(payoff.solutions(), payoff.tests());
		Sequence<S> solutions = payoff.solutions();
		for (S s : solutions) {
			Sequence<T> tests = payoff.tests();
			for (T t : tests) {
				builder.put(s, t, payoff.get(s, t) / testsSum.get(t));
			}
		}
		return builder.build();
	}

	public static <S, T> PayoffTable<S, T> normalize(final PayoffTable<S, T> payoff) {
		PayoffTableBuilder<S, T> builder = PayoffTable.create(payoff.solutions(), payoff.tests());
		int cnt = 0;
		for (T t : payoff.tests()) {
			List<Double> column = payoff.testPayoffs(t).toList();
			Double min = min(column);
			Double max = max(column);
			if (min > 0 || max < 1) {
				cnt++;
			}
			for (S s : payoff.solutions()) {
				builder.put(s, t, (payoff.get(s, t) - min) / (max - min));
			}
		}
		if (cnt != 0) {
			System.out.println("Normalized: " + cnt);
		}
		return builder.build();
	}

	public static <S, T> PayoffTable<S, T> meanNormalize(final PayoffTable<S, T> payoff) {
		PayoffTableBuilder<S, T> builder = PayoffTable.create(payoff.solutions(), payoff.tests());
		for (T t : payoff.tests()) {
			double[] column = Doubles.toArray(payoff.testPayoffs(t).toList());
			double mean = mean(column);
			double stddev = sqrt(variance(column));
			for (S s : payoff.solutions()) {
				if (stddev != 0) {
					builder.put(s, t, (payoff.get(s, t) - mean) / stddev);
				} else {
					builder.put(s, t, payoff.get(s, t));
				}

			}
		}
		return builder.build();
	}

	public static <S, T> PayoffTable<S, T> transform(final PayoffTable<S, T> payoff) {
		DenseMatrix64F eye = identity(payoff.tests().size());
		scale(1.3, eye);

		DenseMatrix64F m = new DenseMatrix64F(payoff.tests().size(), payoff.tests().size());
		CommonOps.fill(m, -0.3);
		CommonOps.add(m, eye, m);

		DenseMatrix64F interactionMatrix = new DenseMatrix64F(payoff.toArray());
		DenseMatrix64F interactionMatrix2 = new DenseMatrix64F(payoff.toArray());
		SimpleMatrix mult = SimpleMatrix.wrap(interactionMatrix).mult(SimpleMatrix.wrap(m));
		CommonOps.solve(mult.transpose().getMatrix(), SimpleMatrix.wrap(interactionMatrix).transpose().getMatrix(),
			interactionMatrix2);
		SimpleMatrix res = SimpleMatrix.wrap(interactionMatrix2).transpose().mult(mult.extractVector(false, 0));

		PayoffTableBuilder<S, T> builder = PayoffTable.create(payoff.solutions(), payoff.tests());
		for (Pair<Integer, S> s : payoff.solutions().enumerate()) {
			for (Pair<Integer, T> t : payoff.tests().enumerate()) {
				builder.put(s.second(), t.second(), mult.get(s.first(), t.first()));
			}
		}
		return builder.build();
	}
}
