package put.ci.cevo.experiments.reports.tables;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.model.ParametrizedModel;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.RowListStarTable;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.ImmutableList;

public class ParametersTableBuilder {

	private static final ColumnInfo MODEL_ID = new ColumnInfo("model_id", Integer.class, "id");
	private static final ColumnInfo MODEL_LABEL = new ColumnInfo("model_label", String.class, "label");

	public <S, T> StarTable createTable(ConfiguredExperiment experiment) {
		ColumnInfo nameColumn = new ColumnInfo("parameter_name");
		ColumnInfo valueColumn = new ColumnInfo("parameter_value");
		List<ColumnInfo> schema = ImmutableList.of(MODEL_ID, MODEL_LABEL, nameColumn, valueColumn);

		RowListStarTable table = new RowListStarTable(schema.toArray(new ColumnInfo[schema.size()]));
		List<Model> models = experiment.getModels();
		for (int i = 0; i < models.size(); ++i) {
			Model model = models.get(i);
			ParametrizedModel parametrizedModel = new ParametrizedModel(model.getAlgorithm());
			for (String parameterName : parametrizedModel.getParametersNames()) {
				Object parameterValue = parametrizedModel.getCachedParameter(parameterName);
				String parameterString;
				if (parameterValue instanceof Class<?>) {
					parameterString = ((Class<?>) parameterValue).getSimpleName();
				} else {
					parameterString = ObjectUtils.toString(parameterValue);
				}

				Object[] row = new Object[] { i, model.getName(), parameterName, parameterString };
				table.addRow(row);
			}
		}
		return table;
	}
}
