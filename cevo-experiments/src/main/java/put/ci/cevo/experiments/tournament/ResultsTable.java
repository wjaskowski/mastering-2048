package put.ci.cevo.experiments.tournament;

import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.util.Collection;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transforms;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class ResultsTable<S, T> {

	public static class ResultTableBuilder<S, T> {

		private final Table<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> resultsTable;

		private ResultTableBuilder(Table<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> resultsTable) {
			this.resultsTable = resultsTable;
		}

		public DescriptiveStatistics put(S team1, T team2, DescriptiveStatistics stats) {
			return resultsTable.put(wrap(team1), wrap(team2), stats);
		}

		public ResultsTable<S, T> build() {
			return new ResultsTable<>(resultsTable);
		}
	}

	private final Table<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> resultTable;

	private ResultsTable(Table<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> resultsTable) {
		this.resultTable = resultsTable;
	}

	public Sequence<S> teamsA() {
		return seq(resultTable.rowKeySet()).map(Transforms.<S> identityUnwrap());
	}

	public Sequence<T> teamsB() {
		return seq(resultTable.columnKeySet()).map(Transforms.<T> identityUnwrap());
	}

	public DescriptiveStatistics get(S first, T second) {
		return resultTable.get(wrap(first), wrap(second));
	}

	public Collection<DescriptiveStatistics> teamAResults(S team) {
		return seq(resultTable.row(wrap(team)).values()).filter(notNull()).asCollection();
	}

	public Collection<DescriptiveStatistics> teamBResults(T team) {
		return seq(resultTable.column(wrap(team)).values()).filter(notNull()).asCollection();
	}

	@Override
	public String toString() {
		return resultTable.toString();
	}

	public static <S, T> ResultTableBuilder<S, T> create(Iterable<S> teamsA, Iterable<T> teamsB) {
		return new ResultTableBuilder<S, T>(ArrayTable.<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> create(
			seq(teamsA).map(Transforms.<S> identityWrap()), seq(teamsB).map(Transforms.<T> identityWrap())));
	}

	public static <S, T> ResultTableBuilder<S, T> create(int expectedSolutions, int expectedTests) {
		return new ResultTableBuilder<S, T>(HashBasedTable.<Wrapper<S>, Wrapper<T>, DescriptiveStatistics> create(
			expectedSolutions, expectedTests));
	}

	private static <V> Wrapper<V> wrap(V object) {
		return Equivalence.identity().wrap(object);
	}
}