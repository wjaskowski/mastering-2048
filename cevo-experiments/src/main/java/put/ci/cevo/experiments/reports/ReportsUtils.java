package put.ci.cevo.experiments.reports;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import uk.ac.starlink.ttools.Stilts;

public class ReportsUtils {

	static {
		final Logger starlinkLogger = Logger.getLogger("uk.ac.starlink.util");
		starlinkLogger.setLevel(Level.SEVERE);
	}

	public static void csvToText(final File csvFile, final File textFile) {
		textFile.getParentFile().mkdirs();
		Stilts.main(new String[] { "-batch", "tpipe", "ifmt=csv", "ofmt=text", "cmd=tablename ''",
			"in=" + csvFile.getPath(), "out=" + textFile.getPath() });
	}

	public static void csvToLatex(final File csvFile, final File latexFile) {
		latexFile.getParentFile().mkdirs();
		Stilts.main(new String[] { "-batch", "tpipe", "ifmt=csv", "ofmt=latex", "cmd=tablename ''",
			"in=" + csvFile.getPath(), "out=" + latexFile.getPath() });
	}
}
