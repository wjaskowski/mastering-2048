package put.ci.cevo.framework.algorithms.common;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.sequence.transforms.Transforms;

import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.intAdd;

/**
 * Actually we do not per-interaction effort anywhere. What we actually need is the total effort made by
 * PopulationEvaluator (TODO). But the first attempt to sort it out failed because of some quirks
 */

public class EffortTable<S, T> {

	public static class EffortTableBuilder<S, T> {

		private final Table<Wrapper<S>, Wrapper<T>, Integer> effortTable;

		private EffortTableBuilder(Table<Wrapper<S>, Wrapper<T>, Integer> effortTable) {
			this.effortTable = effortTable;
		}

		public void put(S solution, T test, InteractionResult result) {
			put(solution, test, result.getEffort());
		}

		public void put(S solution, T test, int effort) {
			effortTable.put(wrap(solution), wrap(test), effort);
		}

		public EffortTable<S, T> build() {
			return new EffortTable<>(effortTable);
		}
	}

	private final Table<Wrapper<S>, Wrapper<T>, Integer> effortTable;

	private EffortTable(Table<Wrapper<S>, Wrapper<T>, Integer> effortTable) {
		this.effortTable = effortTable;
	}

	public int computeEffort(S solution) {
		return seq(effortTable.row(wrap(solution)).values()).filter(notNull()).aggregate(0, intAdd());
	}

	public EffortTable<T, S> transpose() {
		return new EffortTable<>(Tables.transpose(effortTable));
	}

	@Override
	public String toString() {
		return effortTable.toString();
	}

	public static <S, T> EffortTableBuilder<S, T> create(Iterable<S> solutions, Iterable<T> tests) {
		return new EffortTableBuilder<S, T>(ArrayTable.<Wrapper<S>, Wrapper<T>, Integer> create(
			seq(solutions).map(Transforms.<S> identityWrap()), seq(tests).map(Transforms.<T> identityWrap())));
	}

	public static <S, T> EffortTableBuilder<S, T> create(int expectedSolutions, int expectedTests) {
		return new EffortTableBuilder<S, T>(HashBasedTable.<Wrapper<S>, Wrapper<T>, Integer> create(expectedSolutions,
			expectedTests));
	}

	private static <V> Wrapper<V> wrap(V object) {
		return Equivalence.identity().wrap(object);
	}
}
