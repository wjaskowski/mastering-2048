package put.ci.cevo.framework.algorithms.common;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.*;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.List;
import java.util.Set;

import static put.ci.cevo.util.TableUtil.tableToString;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class PayoffTable<S, T> {

	public static class PayoffTableBuilder<S, T> {

		private final Table<Wrapper<S>, Wrapper<T>, Double> payoffTable;

		private PayoffTableBuilder(Table<Wrapper<S>, Wrapper<T>, Double> payoffTable) {
			this.payoffTable = payoffTable;
		}

		public Double put(S solution, T test, double payoff) {
			return payoffTable.put(wrap(solution), wrap(test), payoff);
		}

		public PayoffTableBuilder<T, S> transpose() {
			return new PayoffTableBuilder<>(Tables.transpose(HashBasedTable.create(payoffTable)));
		}

		public PayoffTableBuilder<S, T> inverseValues() {
			Set<Wrapper<S>> rowKeySet = payoffTable.rowKeySet();
			for (Wrapper<S> s : rowKeySet) {
				for (Wrapper<T> t : payoffTable.columnKeySet()) {
					payoffTable.put(s, t, 1 - payoffTable.get(s, t));
				}
			}
			return this;
		}

		public PayoffTable<S, T> build() {
			return new PayoffTable<>(payoffTable);
		}

	}

	private final ArrayTable<Wrapper<S>, Wrapper<T>, Double> payoffTable;

	private PayoffTable(Table<Wrapper<S>, Wrapper<T>, Double> payoffTable) {
		this.payoffTable = ArrayTable.create(payoffTable);
	}

	public Sequence<S> solutions() {
		return seq(payoffTable.rowKeyList()).map(Transforms.<S> identityUnwrap());
	}

	public Sequence<T> tests() {
		return seq(payoffTable.columnKeyList()).map(Transforms.<T> identityUnwrap());
	}

	public Double get(S solution, T test) {
		return payoffTable.get(wrap(solution), wrap(test));
	}

	public Sequence<Double> solutionPayoffs(S solution) {
		return seq(payoffTable.row(wrap(solution)).values()).filter(notNull());
	}

	public Sequence<Double> testPayoffs(T test) {
		return seq(payoffTable.column(wrap(test)).values()).filter(notNull());
	}

	public Double[][] toDoubleArray() {
		return ArrayTable.create(payoffTable).toArray(Double.class);
	}

	public double[][] toArray() {
		double[][] array = new double[solutions().size()][tests().size()];
		for (Pair<Integer, S> solution : solutions().enumerate()) {
			for (Pair<Integer, T> test : tests().enumerate()) {
				array[solution.first()][test.first()] = get(solution.second(), test.second());
			}
		}
		return array;
	}

	@Override
	public String toString() {
		TableBuilder builder = new TableBuilder();
		builder.addIndentedHeaders(tests().toArray());
		for (S s : solutions()) {
			List<Object> row = Lists.newArrayList();
			row.add(s.toString());
			row.addAll(solutionPayoffs(s).asCollection());
			builder.addRow(row);
		}
		return tableToString(builder.build());
	}

	public static <S, T> PayoffTableBuilder<S, T> create(Iterable<S> solutions, Iterable<T> tests) {
		return new PayoffTableBuilder<S, T>(ArrayTable.<Wrapper<S>, Wrapper<T>, Double> create(
			seq(solutions).map(Transforms.<S> identityWrap()), seq(tests).map(Transforms.<T> identityWrap())));
	}

	public static <S, T> PayoffTableBuilder<S, T> create(int expectedSolutions, int expectedTests) {
		return new PayoffTableBuilder<S, T>(HashBasedTable.<Wrapper<S>, Wrapper<T>, Double> create(expectedSolutions,
			expectedTests));
	}

	private static <V> Wrapper<V> wrap(V object) {
		return Equivalence.identity().wrap(object);
	}

}
