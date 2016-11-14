package put.ci.cevo.experiments.reports;

import java.io.File;

import org.apache.log4j.Logger;

import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.RetrospectionTable;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class RetrospectionReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(RetrospectionReport.class);

	private final SerializationManager manager = SerializationManagerFactory.create();

	@AccessedViaReflection
	public RetrospectionReport(File outputDir) {
		super(outputDir);
	}

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), "results");
		final File analysisDir = new File(resultsDir, "analysis");

		logger.info("Saving model analysis to: " + analysisDir);
		RetrospectionTable table = experiment.getRetrospectionTable();
		for (String task : table.tasks()) {
			File taskDir = new File(analysisDir, task);
			for (String model : table.models()) {
				RetrospectionResult result = table.get(task, model);
				try {
					manager.serialize(result,
						createReportFile(taskDir, model + "-analysis.bin", experiment.getUniqueId()));
				} catch (SerializationException e) {
					logger.error("Fatal error while trying to save analysis for model: " + model, e);
				}
			}
		}
	}
}
