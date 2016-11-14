package put.ci.cevo.experiments.reports;

import static org.apache.commons.io.FileUtils.openOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import put.ci.cevo.util.Description;

public class ExperimentConfiguration extends ConfiguredExperimentReport {

	private static final Logger logger = Logger.getLogger(ExperimentConfiguration.class);
	private static final String CONFIG_DIR = "/configuration";

	@Override
	public void generate() {
		PrintWriter cfgWriter = null;
		try {
			File descriptionFile = createReportFile(new File(getOutputDir() + CONFIG_DIR), "config.properties",
				experiment.getUniqueId());
			logger.info("Saving experiment configuration to: " + descriptionFile);

			cfgWriter = new PrintWriter(openOutputStream(descriptionFile));
			cfgWriter.println();
			cfgWriter.println("# This is an automatically generated experiment configuration file.");
			cfgWriter.println("# This file can be used to repeat the experiment.");

			for (Description.Element element : experiment.describe().listElements()) {
				switch (element.getType()) {
				case SECTION:
					cfgWriter.println();
					cfgWriter.println("[" + element.getText() + "]");
					cfgWriter.println();
					break;

				case COMMENT:
					cfgWriter.println("; " + element.getText());
					break;

				case PROPERTY:
					cfgWriter.println(element.getText());
					break;
				}
			}
			cfgWriter.println();
		} catch (IOException e) {
			logger.error("A fatal error occured while generating a configuration file!", e);
		} finally {
			if (cfgWriter != null) {
				cfgWriter.close();
			}
		}
	}

}
