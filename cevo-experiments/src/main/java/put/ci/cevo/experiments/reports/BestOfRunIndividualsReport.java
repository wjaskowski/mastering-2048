package put.ci.cevo.experiments.reports;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;

import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.algorithms.history.EvolutionHistory;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class BestOfRunIndividualsReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(BestOfRunIndividualsReport.class);

	private static final String INDIVIDUALS_DIR = "individuals";

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), INDIVIDUALS_DIR);

		logger.info("Saving best of run individuals to: " + resultsDir);

		SerializationManager sm = SerializationManagerFactory.create();
		for (Model model : experiment.getModels()) {
			EvolutionHistory history = model.getHistory();
			EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(history.getEvolutionHistory());
			try {
				Path path = Paths.get(resultsDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId());
				sm.serialize(processor.bestSolutionOfLastGeneration(), path.toFile());
			} catch (SerializationException e) {
				logger.error("Fatal error while trying to save history for model: " + model.getName(), e);
			}
		}
	}
}
