package put.ci.cevo.experiments.reports;

import java.io.File;

import put.ci.cevo.experiments.ConfiguredExperiment;

public abstract class ConfiguredExperimentReport implements Report {

	private static final File DEFAULT_OUTPUT_DIR = new File(System.getProperty("java.io.tmpdir"));

	protected ConfiguredExperiment experiment;
	protected File outputDir;

	protected ConfiguredExperimentReport() {
		this(DEFAULT_OUTPUT_DIR);
	}

	protected ConfiguredExperimentReport(File outputDir) {
		this.outputDir = outputDir;
	}

	public ConfiguredExperiment getExperiment() {
		return experiment;
	}

	public void setExperiment(ConfiguredExperiment experiment) {
		this.experiment = experiment;
	}

	public File getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(File outputDir) {
		if (outputDir == null || !outputDir.isDirectory()) {
			throw new RuntimeException("The output path: '" + outputDir + "' needs to be a directory!");
		}
		this.outputDir = outputDir;
	}

	public boolean isOutputDirSet() {
		return !outputDir.equals(DEFAULT_OUTPUT_DIR);
	}

	protected File createReportFile(File dir, String name, long id) {
		dir.mkdirs();
		File file = new File(dir, "run-" + id + "-" + name);
		if (file.exists()) {
			throw new RuntimeException("An attempt to override existing data detected! File: " + file
				+ " already exists!");
		}
		return file;
	}

}
