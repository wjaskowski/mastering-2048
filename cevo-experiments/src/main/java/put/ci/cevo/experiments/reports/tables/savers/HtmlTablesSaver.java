package put.ci.cevo.experiments.reports.tables.savers;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.log4j.lf5.util.StreamUtils;

import put.ci.cevo.experiments.reports.TablesReport;
import put.ci.cevo.util.Pair;
import put.ci.cevo.util.UTF8HtmlTableWriter;
import uk.ac.starlink.table.StarTable;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class HtmlTablesSaver implements TablesReportSaver {

	private static final String HTML_TABLES_FILE = "results.html";

	private final UTF8HtmlTableWriter htmlTableWriter = new UTF8HtmlTableWriter(false, false);
	private final Multimap<String, Pair<String, StarTable>> tablesByTasks = ArrayListMultimap.create();

	@Override
	public void writeHeader(PrintWriter writer, PrintStream output) throws IOException {
		writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">");
		writer.println("<html>\n<head>\n<title>Results</title>\n<style type=\"text/css\">");
		StreamUtils.copy(TablesReport.class.getResourceAsStream("ResultsTables.css"), output);
		writer.println("</style>\n</head>\n<body>");
	}

	@Override
	public void saveTotalResultsTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("<h2>Results</h2>");
		writer.println("<h3>Total results for models</h3>");
		htmlTableWriter.writeStarTable(table, output);
		writer.println("<br/>");
	}

	@Override
	public void saveTasksTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("<h3>Tasks for models</h3>");
		htmlTableWriter.writeStarTable(table, output);
		writer.println("<br/>");
	}

	@Override
	public void saveParametersTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException {
		writer.println("<h2>Models</h2>");
		writer.println("<h3>Parameters for models</h3>");
		htmlTableWriter.writeStarTable(table, output);
		writer.println("<br/>");
	}

	@Override
	public void addTable(StarTable table, String taskName, String modelName) {
		tablesByTasks.put(taskName, Pair.create(modelName, table));
	}

	@Override
	public void saveDetailedTables(PrintWriter writer, PrintStream output) throws IOException {
		writer.println("<h3>Detailed results for tasks</h3>");
		for (String task : tablesByTasks.keySet()) {
			writer.println("<h2>Results for task: " + task + "</h2>");
			for (Pair<String, StarTable> elem : tablesByTasks.get(task)) {
				writer.println("<h1>Model: " + elem.first() + "</h1>");
				htmlTableWriter.writeStarTable(elem.second(), output);
				writer.println("<br/>");
			}
			writer.println("<br/>");
		}
		tablesByTasks.clear();
	}

	@Override
	public void finalizeDocument(PrintWriter writer) {
		writer.println("</body>\n</html>");
	}

	@Override
	public String getReportName() {
		return HTML_TABLES_FILE;
	}

}
