package put.ci.cevo.experiments.reports;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TimeUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EvolutionTimeReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(EvolutionTimeReport.class);

	private static final String EVOLUTION_TIME_DIR = "evolution-times";

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), EVOLUTION_TIME_DIR);

		logger.info("Saving evolution times to: " + resultsDir);
		TableUtil.TableBuilder builder = new TableUtil.TableBuilder("model", "evolution_time");
		for (Model model : experiment.getModels()) {
			List<EvolutionState> states = model.getHistory().getEvolutionHistory();
			builder.addRow(model.getName(), TimeUtils.millisToSeconds(states.get(states.size() - 1).getElapsedTime()));
		}
		Path path = Paths.get(resultsDir.getAbsolutePath(), "run-" + experiment.getUniqueId() + ".csv");
		TableUtil.saveTableAsCSV(builder.build(), path.toFile());
	}
}
