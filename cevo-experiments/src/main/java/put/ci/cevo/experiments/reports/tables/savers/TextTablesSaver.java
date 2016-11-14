package put.ci.cevo.experiments.reports.tables.savers;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.UTF8TextTableWriter;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class TextTablesSaver implements TablesReportSaver {

	private static final String TEXT_TABLES_FILE = "results.txt";

	private final UTF8TextTableWriter textTableWriter = new UTF8TextTableWriter();
	private final Multimap<String, Pair<String, StarTable>> tablesByTasks = ArrayListMultimap.create();

	@Override
	public void writeHeader(PrintWriter writer, PrintStream output) throws IOException {
		writer.println("\n*** RESULTS ***\n");
	}

	@Override
	public void saveTotalResultsTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("*** Total results for models ***");
		textTableWriter.writeStarTable(table, output);
		writer.println();
	}

	@Override
	public void saveTasksTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("*** Tasks for models ***");
		textTableWriter.writeStarTable(table, output);
		writer.println();
	}

	@Override
	public void saveParametersTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("*** Parameters for models ***");
		textTableWriter.writeStarTable(table, output);
		writer.println();
	}

	@Override
	public void addTable(StarTable table, String taskName, String modelName) {
		tablesByTasks.put(taskName, Pair.create(modelName, table));
	}

	@Override
	public void saveDetailedTables(PrintWriter writer, PrintStream output) throws IOException {
		writer.println("*** Detailed results for tasks ***");
		for (String task : tablesByTasks.keySet()) {
			writer.println("Results for task: " + task);
			for (Pair<String, StarTable> elem : tablesByTasks.get(task)) {
				writer.println("Model: " + elem.first());
				textTableWriter.writeStarTable(elem.second(), output);
				writer.println();
			}
			writer.println();
		}
		tablesByTasks.clear();
	}

	@Override
	public void finalizeDocument(PrintWriter writer) {
		// ignore
	}

	@Override
	public String getReportName() {
		return TEXT_TABLES_FILE;
	}

}
