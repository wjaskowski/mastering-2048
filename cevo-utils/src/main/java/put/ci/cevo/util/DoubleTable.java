package put.ci.cevo.util;

import com.google.common.base.Equivalence;
import com.google.common.base.Equivalence.Wrapper;
import com.google.common.collect.*;
import com.google.common.collect.Lists;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transforms;

import java.util.List;

import static put.ci.cevo.util.TableUtil.tableToString;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.seq;


// TODO: rename it to IdentityTable, make it fully generic and use it in PayoffTable implementation
public class DoubleTable<S, T> {

	public static class DoubleTableBuilder<S, T> {

		private final Table<Wrapper<S>, Wrapper<T>, Double> table;

		private DoubleTableBuilder(Table<Wrapper<S>, Wrapper<T>, Double> table) {
			this.table = table;
		}

		public Double put(S row, T col, double payoff) {
			return table.put(wrap(row), wrap(col), payoff);
		}

		public DoubleTableBuilder<T, S> transpose() {
			return new DoubleTableBuilder<>(Tables.transpose(HashBasedTable.create(table)));
		}

		public DoubleTable<S, T> build() {
			return new DoubleTable<>(table);
		}

	}

	private final ArrayTable<Wrapper<S>, Wrapper<T>, Double> table;

	private DoubleTable(Table<Wrapper<S>, Wrapper<T>, Double> table) {
		this.table = ArrayTable.create(table);
	}

	public Sequence<Double> rowValues(S row) {
		return seq(table.row(wrap(row)).values()).filter(notNull());
	}

	private static <V> Wrapper<V> wrap(V object) {
		return Equivalence.identity().wrap(object);
	}

	public Sequence<Double> colValues(T col) {
		return seq(table.column(wrap(col)).values()).filter(notNull());
	}

	public Double[][] toDoubleArray() {
		return ArrayTable.create(table).toArray(Double.class);
	}

	public double[][] toArray() {
		double[][] array = new double[rows().size()][cols().size()];
		for (Pair<Integer, S> row : rows().enumerate()) {
			for (Pair<Integer, T> col : cols().enumerate()) {
				array[row.first()][col.first()] = get(row.second(), col.second());
			}
		}
		return array;
	}

	public Sequence<S> rows() {
		return seq(table.rowKeyList()).map(Transforms.<S>identityUnwrap());
	}

	public Sequence<T> cols() {
		return seq(table.columnKeyList()).map(Transforms.<T>identityUnwrap());
	}

	public Double get(S row, T col) {
		return table.get(wrap(row), wrap(col));
	}

	@Override
	public String toString() {
		TableBuilder builder = new TableBuilder();
		builder.addIndentedHeaders(cols().toArray());
		for (S s : rows()) {
			List<Object> row = Lists.newArrayList();
			row.add(s.toString());
			row.addAll(rowValues(s).asCollection());
			builder.addRow(row);
		}
		return tableToString(builder.build());
	}

	public static <S, T> DoubleTableBuilder<S, T> create(Iterable<S> rows, Iterable<T> cols) {
		return new DoubleTableBuilder<>(ArrayTable
				.<Wrapper<S>, Wrapper<T>, Double>create(seq(rows).map(Transforms.<S>identityWrap()),
						seq(cols).map(Transforms.<T>identityWrap())));
	}

	public static <S, T> DoubleTableBuilder<S, T> create(int expectedRows, int expectedCols) {
		return new DoubleTableBuilder<>(
				HashBasedTable.<Wrapper<S>, Wrapper<T>, Double>create(expectedRows, expectedCols));
	}

}
