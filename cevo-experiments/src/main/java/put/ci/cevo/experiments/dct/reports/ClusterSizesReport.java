package put.ci.cevo.experiments.dct.reports;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.experiments.reports.ConfiguredExperimentReport;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.stats.EventsLogger;
import put.ci.cevo.util.stats.TableEventHandler;
import uk.ac.starlink.table.StarTable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static put.ci.cevo.experiments.clustering.aggregates.ClusteringFitnessAggregate.*;
import static put.ci.cevo.ml.clustering.algorithms.XMeansClusterer.NumClusters;

public class ClusterSizesReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(ClusterSizesReport.class);

	@Override
	public void generate() {
		final File resultsDir = new File(getOutputDir(), "clustering-stats");
		final File corrDir = new File(getOutputDir(), "clustering-corr");
		final File varDir = new File(getOutputDir(), "clustering-var");
		final File sepDir = new File(getOutputDir(), "clustering-sep");
		final File sizeDir = new File(getOutputDir(), "clustering-xsize");
		logger.info("Saving clustering statistics to: " + resultsDir);

		for (Model model : experiment.getModels()) {
			EventsLogger eventsLogger = model.getEventsLogger();
			// TODO: This could be implemented with MVC
			StarTable table = eventsLogger.getHandler(ClusteringStats.class, TableEventHandler.class).getTable();
			if (table != null) {
				Path path = Paths.get(resultsDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(table, path.toFile());
			}

			table = eventsLogger.getHandler(ClustersCorrelation.class, TableEventHandler.class).getTable();
			if (table != null) {
				Path path = Paths.get(corrDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(table, path.toFile());
			}

			table = eventsLogger.getHandler(ClustersVariance.class, TableEventHandler.class).getTable();
			if (table != null) {
				Path path = Paths.get(varDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(table, path.toFile());
			}

			table = eventsLogger.getHandler(ClustersCohesionSepration.class, TableEventHandler.class).getTable();
			if (table != null) {
				Path path = Paths.get(sepDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(table, path.toFile());
			}

			table = eventsLogger.getHandler(NumClusters.class, TableEventHandler.class).getTable();
			if (table != null) {
				Path path = Paths.get(sizeDir.getAbsolutePath(), model.getName(), "run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(table, path.toFile());
			}
		}
	}

}
