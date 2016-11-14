package put.ci.cevo.newexperiments;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import put.ci.cevo.util.ReflectionUtils;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;

public class ExperimentRunner {

	private static final Logger logger = Logger.getLogger(ExperimentRunner.class);

	private static final Configuration config = Configuration.getConfiguration();

	public static final File RESULTS_DIR = config.getFile(new ConfKey("results_dir"), new File("."));

	private static Experiment createExperiment(String className) throws InstantiationException, IllegalAccessException {
		return (Experiment) ReflectionUtils.forName(className).newInstance();
	}

	/**
	 * @param args
	 *            Experiment class name (must implement Experiment interface)
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		setupLogger(RESULTS_DIR);
		Experiment experiment = config.getObject(new ConfKey("experiment"), null);

		int argumentsStart = 0;

		if (experiment == null) {
			// Fallback to old format: read as the first argument
			String experimentClassName = args[0];
			experiment = createExperiment(experimentClassName);
			argumentsStart = 1;
		}
		logger.info("Executing experiment: " + experiment.getClass().toString());

		experiment.run(ArrayUtils.subarray(args, argumentsStart, args.length));
	}

	private static void setupLogger(File resultsDirectory) {
		try {
			FileUtils.forceMkdir(resultsDirectory);
			FileWriter fileWriter = new FileWriter(new File(resultsDirectory, "run.log"));
			PatternLayout logPattern = new PatternLayout("%-10r [%-5p] [%t] (%F:%L) -- %m%n");
			WriterAppender logAppender = new WriterAppender(logPattern, fileWriter);
			Logger.getRootLogger().addAppender(logAppender);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
