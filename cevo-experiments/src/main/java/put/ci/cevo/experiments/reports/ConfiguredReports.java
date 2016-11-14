package put.ci.cevo.experiments.reports;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;

import com.google.common.io.Files;

public class ConfiguredReports extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(ConfiguredReports.class);
	private static final Configuration configuration = Configuration.getConfiguration();

	public static final ConfigurationKey REPORTS = new ConfKey("reports");
	public static final ConfigurationKey REPORTS_OUTPUT_PATH = new ConfKey("reports.outputPath");
	public static final ConfigurationKey REPORTS_CREATE_SUBDIR = new ConfKey("reports.createSubdir");
	public static final ConfigurationKey REPORTS_UNIQUE_SUBDIR = new ConfKey("reports.uniqueSubdir");

	public ConfiguredReports(ConfiguredExperiment experiment) {
		setExperiment(experiment);
	}

	@Override
	public void generate() {
		setupOutputPath(experiment);
		if (!configuration.getImmediateSubKeys(REPORTS).toList().isEmpty()) {
			List<ConfiguredExperimentReport> reports = configuration.createConfiguredObjects(REPORTS);
			for (ConfiguredExperimentReport report : reports) {
				report.setExperiment(experiment);
				if (!report.isOutputDirSet()) {
					report.setOutputDir(this.getOutputDir());
				}
				try {
					report.generate();
				} catch (Exception e) {
					logger.error("Error when running report " + report, e);
				}
			}
		}
		copyConfigFile();
	}

	private void setupOutputPath(ConfiguredExperiment experiment) {
		if (configuration.containsKey(REPORTS_OUTPUT_PATH)) {
			File outputPath = new File(configuration.getString(REPORTS_OUTPUT_PATH));
			if (configuration.getBoolean(REPORTS_CREATE_SUBDIR, false)) {
				outputPath = getSubdir(outputPath, experiment);
			}
			if (configuration.getBoolean(REPORTS_UNIQUE_SUBDIR, false)) {
				outputPath = getUniqueSubDir(outputPath, experiment);
			}
			outputPath.mkdirs();
			setOutputDir(outputPath);
		} else {
			logger.warn("Explicit output path not set! Using: " + getOutputDir().getAbsolutePath());
		}
	}

	private File getUniqueSubDir(File outputPath, ConfiguredExperiment experiment) {
		File file = new File(outputPath, "run-" + experiment.getUniqueId());
		if (file.exists()) {
			throw new RuntimeException("Failed to create a unique subdir, path: " + file
				+ " already denotes an existing directory");
		}
		return file;
	}

	private File getSubdir(File outputPath, ConfiguredExperiment experiment) {
		String time = new DateTime(experiment.getStartTime()).toString("YYYY-MM-dd_HHmm");
		return new File(outputPath, experiment.getName() + "_" + time);
	}

	private void copyConfigFile() {
		File configFile = configuration.getFile(new ConfKey("framework.properties"));
		if (configFile != null) {
			try {
				Files.copy(configFile, new File(getOutputDir(), configFile.getName()));
			} catch (IOException e) {
				logger.error("Unable to copy configuration file: " + configFile + " to: " + getOutputDir());
			}
		}
	}

}
