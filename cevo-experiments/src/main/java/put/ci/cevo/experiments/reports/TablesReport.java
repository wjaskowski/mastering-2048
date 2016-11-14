package put.ci.cevo.experiments.reports;

import static java.nio.charset.Charset.forName;
import static org.apache.commons.io.FileUtils.openOutputStream;
import static put.ci.cevo.experiments.reports.ReportsUtils.csvToLatex;

import java.io.File;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.experiments.reports.tables.DetailedResultsTable;
import put.ci.cevo.experiments.reports.tables.ParametersTableBuilder;
import put.ci.cevo.experiments.reports.tables.TasksTableBuilder;
import put.ci.cevo.experiments.reports.tables.TotalResultsTableBuilder;
import put.ci.cevo.experiments.reports.tables.savers.CsvTableSaver;
import put.ci.cevo.experiments.reports.tables.savers.TableSaver;
import put.ci.cevo.experiments.reports.tables.savers.TablesReportSaver;
import put.ci.cevo.experiments.reports.tables.savers.TextTablesSaver;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.RetrospectionTable;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import uk.ac.starlink.table.StarTable;

public class TablesReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(TablesReport.class);

	public static final String MODELS_PARAMETERS_FILE = "models_parameters";
	public static final String MODELS_RESULTS_FILE = "models_results";
	public static final String TASKS_FILE = "tasks_parameters";

	private final TablesReportSaver tablesReport;
	private final TableSaver csvSaver;

	@AccessedViaReflection
	public TablesReport() {
		this(new TextTablesSaver());
	}

	@AccessedViaReflection
	public TablesReport(TablesReportSaver tablesReport) {
		this.tablesReport = tablesReport;
		this.csvSaver = new CsvTableSaver();
	}

	@Override
	public void generate() {
		logger.info("Generating result tables");

		final File resultsDir = new File(getOutputDir(), "results");
		final File csvDir = new File(resultsDir, "csv");
		final File latexDir = new File(resultsDir, "latex");
		final File htmlDir = new File(resultsDir, "html");

		final long run = experiment.getUniqueId();
		RetrospectionTable retrospectionTable = experiment.getRetrospectionTable();
		try (PrintStream output = new PrintStream(openOutputStream(createReportFile(htmlDir, "results.html", run)))) {
			try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, forName("UTF-8")), true)) {
				tablesReport.writeHeader(writer, output);

				StarTable totalTable = makeTotalTable(experiment, retrospectionTable, csvDir, latexDir);
				tablesReport.saveTotalResultsTable(totalTable, writer, output);

				StarTable tasksTable = makeTasksTable(experiment, csvDir, latexDir);
				tablesReport.saveTasksTable(tasksTable, writer, output);

				StarTable parametersTable = makeParametersTable(experiment, csvDir, latexDir);
				tablesReport.saveParametersTable(parametersTable, writer, output);

				for (String task : retrospectionTable.tasks()) {
					for (Model model : experiment.getModels()) {
						RetrospectionResult retrospectionResult = retrospectionTable.get(task, model.getName());
						StarTable detailedTable = makeDetailedTable(model, retrospectionResult, new File(csvDir, task),
							new File(latexDir, task));
						tablesReport.addTable(detailedTable, task, model.getName());
					}
				}
				tablesReport.saveDetailedTables(writer, output);
				tablesReport.finalizeDocument(writer);
			}
		} catch (Exception e) {
			logger.error("Fatal error occured while generating report!", e);
		}

	}

	private StarTable makeParametersTable(ConfiguredExperiment experiment, File csvDir, File latexDir) {
		StarTable parametersTable = new ParametersTableBuilder().createTable(experiment);
		saveTable(new File(csvDir, MODELS_PARAMETERS_FILE), new File(latexDir, MODELS_PARAMETERS_FILE),
			MODELS_PARAMETERS_FILE, parametersTable, true);
		return parametersTable;
	}

	private StarTable makeTasksTable(ConfiguredExperiment experiment, File csvDir, File latexDir) {
		StarTable tasksTable = new TasksTableBuilder().createTable(experiment);
		saveTable(new File(csvDir, TASKS_FILE), new File(latexDir, TASKS_FILE), TASKS_FILE, tasksTable, true);
		return tasksTable;
	}

	private StarTable makeTotalTable(ConfiguredExperiment experiment, RetrospectionTable data, File csvDir,
			File latexDir) {
		StarTable resultsTable = new TotalResultsTableBuilder().createTable(data);
		saveTable(new File(csvDir, MODELS_RESULTS_FILE), new File(latexDir, MODELS_RESULTS_FILE), MODELS_RESULTS_FILE,
			resultsTable, true);

		return resultsTable;
	}

	private StarTable makeDetailedTable(Model model, RetrospectionResult data, File taskCsvDir, File taskLatextDir) {
		StarTable detailsTable = new DetailedResultsTable().createTable(model, data, experiment.getUniqueId());
		saveTable(new File(taskCsvDir, model.getName()), new File(taskLatextDir, model.getName()), model.getName(),
			detailsTable, false);

		return detailsTable;
	}

	private void saveTable(File csvDir, File latexDir, String name, StarTable resultsTable, boolean reportFile) {
		File resultsCsv, resultsLatex;
		if (reportFile) {
			resultsCsv = createReportFile(csvDir, name + ".csv", experiment.getUniqueId());
			resultsLatex = createReportFile(latexDir, name + ".tex", experiment.getUniqueId());
		} else {
			resultsCsv = new File(csvDir, "run-" + experiment.getUniqueId() + ".csv");
			resultsLatex = new File(latexDir, "run-" + experiment.getUniqueId() + ".tex");
		}
		csvSaver.saveTable(resultsTable, resultsCsv);
		csvToLatex(resultsCsv, resultsLatex);
	}

}
