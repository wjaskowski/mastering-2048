package put.ci.cevo.experiments.new2048;

import java.io.*;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.google.common.io.LittleEndianDataOutputStream;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.games.game2048.SpecialBinarySerializer;
import put.ci.cevo.games.game2048.TilingsSet2048;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.newexperiments.ExperimentRunner;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class ConvertToSpecialBinaryFormatExperiment implements Experiment {

	private static final Logger logger = Logger.getLogger(ConfiguredExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();
	private static final SerializationManager serializer = SerializationManagerFactory.create();

	private final File ntuplesFile;

	private enum FunctionKind {
		NORMAL, TILES
	}

	private final FunctionKind FUNCTION_KIND = config.getEnumValue(FunctionKind.class, new ConfKey("ntuples_kind"),
			FunctionKind.TILES);
	private final int FORMAT = config.getInt(new ConfKey("format"), 1);

	public ConvertToSpecialBinaryFormatExperiment() {
		this.ntuplesFile = config.getFile(new ConfKey("ntuples_file"));
	}

	@Override
	public void run(String[] args) {

		Stopwatch stopwatch = Stopwatch.createStarted();
		TilingsSet2048 tiles = null;
		switch (FUNCTION_KIND) {
		case TILES:
			tiles = serializer.deserializeWrapExceptions(ntuplesFile);
			System.out.println("Non-zero weights: " + tiles.countNonZeroWeights());
			break;
		default:
			throw new NotImplementedException();
		}
		System.out.printf("elapsed: %.2fs\n", stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0);

		stopwatch.reset(); stopwatch.start();
		try (LittleEndianDataOutputStream output = new LittleEndianDataOutputStream(new BufferedOutputStream(new FileOutputStream(
				new File(ntuplesFile.getAbsolutePath() + ".special"))))) {
			SpecialBinarySerializer.serialize(tiles, output, FORMAT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.printf("elapsed: %.2fs\n", stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0);
	}

	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		ExperimentRunner.main(args);
	}
}
