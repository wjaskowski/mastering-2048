package put.ci.cevo.experiments.reports;

import static put.ci.cevo.experiments.reports.ConfiguredReports.REPORTS_OUTPUT_PATH;
import static put.ci.cevo.experiments.reports.TablesReport.MODELS_PARAMETERS_FILE;
import static put.ci.cevo.experiments.reports.TablesReport.MODELS_RESULTS_FILE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import put.ci.cevo.util.configuration.Configuration;

import com.google.common.collect.ImmutableList;

public class TablesView extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(TablesView.class);
	private static final Configuration configuration = Configuration.getConfiguration();

	private static final List<String> TABLES_FILES = ImmutableList.of(MODELS_PARAMETERS_FILE, MODELS_RESULTS_FILE);

	@Override
	public void generate() {
		List<String> args = new ArrayList<String>();
		args.add("-noserv");
		args.add("-nocheckvers");

		File resultsDir = new File(getOutputDir() + "/results/csv/");
		for (String tableFileName : TABLES_FILES) {
			File tableFile = new File(resultsDir, tableFileName + ".csv");
			if (tableFile.exists()) {
				args.addAll(Arrays.asList("-f", "csv", tableFile.getAbsolutePath()));
			}
		}

		for (String task : experiment.getRetrospectionTable().tasks()) {
			File tableFile = new File(resultsDir, task + ".csv");
			if (tableFile.exists()) {
				args.addAll(Arrays.asList("-f", "csv", tableFile.getAbsolutePath()));
			}
		}

		File benchmarkFile = new File(getOutputDir() + "/benchmark/benchmark.csv");
		if (benchmarkFile.exists()) {
			args.addAll(Arrays.asList("-f", "csv", benchmarkFile.getAbsolutePath()));
		}

//		try {
//			Driver.main(args.toArray(new String[args.size()]));
//		} catch (SampException e) {
//			throw new RuntimeException(e);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
	}

	public static void main(String[] args) {
		TablesView report = new TablesView();
		if (configuration.containsKey(REPORTS_OUTPUT_PATH)) {
			report.setOutputDir(new File(configuration.getString(REPORTS_OUTPUT_PATH)));
		}
		if (!report.isOutputDirSet()) {
			logger.info("Saving reports to temporary directory: " + report.getOutputDir());
		}
		report.generate();
	}
}
