package put.ci.cevo.experiments.reports.tables;

import static put.ci.cevo.util.TextUtils.format;

import java.util.List;

import put.ci.cevo.framework.retrospection.RetrospectionTable;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowListStarTable;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class TotalResultsTableBuilder {

	private static final ColumnInfo MODEL_ID = new ColumnInfo("model_id", Integer.class, "id");
	private static final ColumnInfo MODEL_LABEL = new ColumnInfo("model_label", String.class, "label");

	public StarTable createTable(RetrospectionTable data) {
		List<ColumnInfo> schema = Lists.newArrayList(MODEL_ID, MODEL_LABEL);
		for (String task : data.tasks()) {
			schema.add(new ColumnInfo(task));
		}
		final RowListStarTable table = new RowListStarTable(schema.toArray(new ColumnInfo[schema.size()]));
		for (String model : data.models()) {
			table.addRow(makeResultsRows(model, data).toArray());
		}
		return table;
	}

	private List<Object> makeResultsRows(String model, RetrospectionTable data) {
		List<String> models = ImmutableList.copyOf(data.models());
		List<Object> row = Lists.newArrayList((Object) models.indexOf(model), model);
		for (String task : data.tasks()) {
			String fitness = format(data.get(task, model).lastGenerationBestFitness());
			row.add(fitness);
		}
		return row;
	}
}
