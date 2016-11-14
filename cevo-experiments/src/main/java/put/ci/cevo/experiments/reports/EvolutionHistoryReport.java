package put.ci.cevo.experiments.reports;

import java.io.File;

import org.apache.log4j.Logger;

import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class EvolutionHistoryReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(EvolutionHistoryReport.class);

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), "results");
		final File historyDir = new File(resultsDir, "history");

		logger.info("Saving evolution history to: " + historyDir);

		SerializationManager sm = SerializationManagerFactory.create();
		for (Model model : experiment.getModels()) {
			final EvolutionHistory history = model.getHistory();
			try {
				File reportFile = createReportFile(new File(historyDir, model.getName()), "history.dump",
					experiment.getUniqueId());
				sm.serialize(history, reportFile);
			} catch (SerializationException e) {
				logger.error("Fatal error while trying to save history for model: " + model.getName(), e);
			}
		}
	}
}
