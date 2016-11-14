package put.ci.cevo.experiments.dct.reports;

import org.apache.log4j.Logger;
import put.ci.cevo.experiments.Model;
import put.ci.cevo.experiments.dct.experiments.DCTModel;
import put.ci.cevo.experiments.reports.ConfiguredExperimentReport;
import put.ci.cevo.util.TableUtil;
import uk.ac.starlink.table.StarTable;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DensityHistogramReport extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(DensityHistogramReport.class);

	@Override
	public void generate() {
		final File histogramDir = new File(getOutputDir(), "density-histograms");
		logger.info("Saving density histograms to: " + histogramDir);

		for (Model model : experiment.getModels()) {
			if (model instanceof DCTModel) {
				DCTModel m = (DCTModel) model;
				StarTable histogramTable = m.getHistogram().createHistogramTable();

				Path path = Paths.get(histogramDir.getAbsolutePath(), model.getName(),
						"run-" + experiment.getUniqueId() + ".csv");
				TableUtil.saveTableAsCSV(histogramTable, path.toFile());
			}
		}
	}
}
