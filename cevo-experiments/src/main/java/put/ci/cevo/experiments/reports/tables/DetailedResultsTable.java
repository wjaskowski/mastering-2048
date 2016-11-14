package put.ci.cevo.experiments.reports.tables;

import static put.ci.cevo.util.sequence.transforms.Transforms.formatDouble;
import static uk.ac.starlink.table.ArrayColumn.makeColumn;

import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import uk.ac.starlink.table.ColumnInfo;
import uk.ac.starlink.table.ColumnStarTable;
import uk.ac.starlink.table.StarTable;

public class DetailedResultsTable {

	private static final ColumnInfo MODEL_LABEL = new ColumnInfo("model_label", String.class, "Label");
	private static final ColumnInfo GENERATION = new ColumnInfo("generation", Integer.class, "Generation");
	private static final ColumnInfo EFFORT = new ColumnInfo("effort", Integer.class, "Effort");

	/**
	 * This table is created per task and model!
	 */
	public <S, T> StarTable createTable(Model model, RetrospectionResult result, long run) {
		EvolutionHistory history = model.getHistory();
		EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history);

		List<Long> generationalEffort = processor.getGenerationalEffort().toList();
		List<Long> reportedEffort = new ArrayList<>();
		for (int gen : result.getFitness().keySet()) {
			reportedEffort.add(generationalEffort.get(gen));
		}

		int numRows = result.getFitness().keySet().size();
		final ColumnStarTable table = ColumnStarTable.makeTableWithRows(numRows);
		table.addColumn(makeColumn(MODEL_LABEL, createLabels(model.getName(), numRows).toArray()));
		table.addColumn(makeColumn(GENERATION, result.getFitness().keySet().toArray()));
		table.addColumn(makeColumn(EFFORT, reportedEffort.toArray()));
		table.addColumn(makeColumn("run-" + run, result.getFitnessValues().map(formatDouble()).toArray()));

		return table;
	}

	private List<Object> createLabels(String modelName, int generations) {
		final List<Object> labels = new ArrayList<Object>();
		for (int i = 0; i < generations; i++) {
			labels.add(modelName);
		}
		return labels;
	}
}
