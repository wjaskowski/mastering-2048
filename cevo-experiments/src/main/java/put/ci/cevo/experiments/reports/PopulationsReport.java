package put.ci.cevo.experiments.reports;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.algorithms.history.EvolutionHistoryProcessor;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PopulationsReport extends ConfiguredExperimentReport {

	private static final SerializationManager manager = SerializationManager.createDefault();
	private static final Logger logger = Logger.getLogger(PopulationsReport.class);

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), "results");
		final File popDir = new File(resultsDir, "pops");

		logger.info("Serializing populations to: " + popDir);
		List<Model> models = experiment.getModels();
		for (Model model : models) {
			EvolutionHistoryProcessor processor = new EvolutionHistoryProcessor(model.getHistory());
			try {
				Path path1 = Paths.get(popDir.getAbsolutePath(), model.getName(), "solutions-run-" + experiment.getUniqueId());
				Path path2 = Paths.get(popDir.getAbsolutePath(), model.getName(), "tests-run-" + experiment.getUniqueId());
				manager.serialize(processor.getSolutionsLastPopulation().toList(), path1.toFile());
				manager.serialize(processor.getTestsLastPopulation().toList(), path2.toFile());
			} catch (SerializationException e) {
				logger.error("Fatal error while trying to save history for model: " + model.getName(), e);
			}
		}
	}

}
