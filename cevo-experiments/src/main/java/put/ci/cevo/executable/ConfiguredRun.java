package put.ci.cevo.executable;

import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.experiments.reports.ConfiguredReports;

import java.io.File;
import java.io.StringWriter;

import static org.apache.commons.io.FileUtils.writeStringToFile;

public class ConfiguredRun {

	/**
	 * Runs the configured experiment.
	 */
	public static <S, T, V> void main(String[] args) throws Exception {
		StringWriter log = teeLog();

		ConfiguredExperiment experiment = new ConfiguredExperiment();
		experiment.run();

		ConfiguredReports report = new ConfiguredReports(experiment);
		report.generate();

		writeStringToFile(new File(report.getOutputDir() + "/logs", "run-" + experiment.getUniqueId() + ".log"),
			log.toString(), "UTF-8");
	}

	private static StringWriter teeLog() {
		StringWriter stringWriter = new StringWriter();
		PatternLayout logPattern = new PatternLayout("%d{ISO8601} [%-5p] [%t] %c -- %m%n");
		WriterAppender logAppender = new WriterAppender(logPattern, stringWriter);
		Logger.getRootLogger().addAppender(logAppender);

		return stringWriter;
	}

}
