package put.ci.cevo.experiments.reports.tables.savers;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import uk.ac.starlink.table.StarTable;

public interface TablesReportSaver {

	public void writeHeader(PrintWriter writer, PrintStream output) throws IOException;

	public void finalizeDocument(PrintWriter writer);

	public void saveTotalResultsTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException;

	public void saveTasksTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException;

	public void saveParametersTable(StarTable table, PrintWriter writer, PrintStream output) throws IOException;

	public void saveDetailedTables(PrintWriter writer, PrintStream output) throws IOException;

	public void addTable(StarTable table, String taskName, String modelName);

	public String getReportName();

}
