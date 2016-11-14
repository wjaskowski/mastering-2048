package put.ci.cevo.experiments.reports.tables;

import static com.google.common.collect.ImmutableList.of;

import java.util.List;

import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.framework.retrospection.tasks.RetrospectionTask;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowListStarTable;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.Lists;

public class TasksTableBuilder {

	public <S, T> StarTable createTable(ConfiguredExperiment experiment) {
		List<ColumnInfo> schema = Lists.newArrayList(new ColumnInfo("task_name"), new ColumnInfo("description"));
		RowListStarTable table = new RowListStarTable(schema.toArray(new ColumnInfo[schema.size()]));
		for (RetrospectionTask task : experiment.getTasks()) {
			table.addRow(of(task.describe().getName(), task.toString()).toArray());
		}
		return table;
	}
}
