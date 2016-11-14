package put.ci.cevo.framework.retrospection;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Collection;
import java.util.Set;

public class RetrospectionTable {

	private final Table<String, String, RetrospectionResult> table;

	private RetrospectionTable(Table<String, String, RetrospectionResult> table) {
		this.table = table;
	}

	public Set<String> tasks() {
		return table.rowKeySet();
	}

	public Set<String> models() {
		return table.columnKeySet();
	}

	public RetrospectionResult get(String task, String model) {
		return table.get(task, model);
	}

	public RetrospectionResult put(String task, String model, RetrospectionResult result) {
		return table.put(task, model, result);
	}

	public Collection<RetrospectionResult> taskResults(String task) {
		return table.row(task).values();
	}

	public Collection<RetrospectionResult> modelResults(String model) {
		return table.column(model).values();
	}

	@Override
	public String toString() {
		return table.toString();
	}

	public static RetrospectionTable create(Iterable<String> tasks, Iterable<String> models) {
		return new RetrospectionTable(ArrayTable.<String, String, RetrospectionResult> create(tasks, models));
	}

	public static RetrospectionTable create(int tasks, int models) {
		return new RetrospectionTable(HashBasedTable.<String, String, RetrospectionResult> create(tasks, models));
	}

}
