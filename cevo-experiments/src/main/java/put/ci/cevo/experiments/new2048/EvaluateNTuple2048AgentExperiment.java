package put.ci.cevo.experiments.new2048;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.ConfiguredExperiment;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.encodings.ntuple.NTuplesStateValueFunction;
import put.ci.cevo.games.game2048.*;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.newexperiments.ExperimentRunner;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.agent.policies.VFunctionControlPolicy;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class EvaluateNTuple2048AgentExperiment implements Experiment {

	private static final Logger logger = Logger.getLogger(ConfiguredExperiment.class);
	private static final Configuration config = Configuration.getConfiguration();
	private static final SerializationManager serializer = SerializationManagerFactory.create();

	private final ThreadedContext context;
	private final File ntuplesFile;
	private final int numSamples;
	private final int depth;

	private enum FunctionKind {
		NORMAL, BIG, TILES
	}

	private final FunctionKind FUNCTION_KIND = config.getEnumValue(FunctionKind.class, new ConfKey("ntuples_kind"),
			FunctionKind.NORMAL);

	public EvaluateNTuple2048AgentExperiment() {
		int seed = config.getSeed(new ConfKey("seed"), 123);
		int threads = config.getInt(new ConfKey("threads"), 1);
		this.context = new ThreadedContext(seed, threads);
		this.ntuplesFile = config.getFile(new ConfKey("ntuples_file"));
		this.numSamples = config.getInt(new ConfKey("num_samples"), 100);
		this.depth = config.getInt(new ConfKey("depth"), 3);
	}

	@Override
	public void run(String[] args) {
		Game2048PerformanceMeasure performanceMeasure = new Game2048PerformanceMeasure(numSamples, System.out::println);

		Agent<State2048, Action2048> agent;
		VFunctionControlPolicy<State2048, Action2048> policy = new Game2048ExpectimaxPolicy(new Game2048(), depth);
		//VFunctionControlPolicy<State2048, Action2048> policy = new Game2048ExpectimaxIterativeDeepeningPolicy(new Game2048(), depth, 10);

		switch (FUNCTION_KIND) {
		case NORMAL:
			NTuples nTuples = serializer.deserializeWrapExceptions(ntuplesFile);
			agent = new AfterstateFunctionAgent<>(new NTuplesStateValueFunction<>(
					nTuples), policy);
			break;
		case TILES:
			TilingsSet2048 tiles = serializer.deserializeWrapExceptions(ntuplesFile);

			long totalWeights = tiles.getTotalWeights();
			long nonZeroWeights = tiles.countNonZeroWeights();
			long[] zeros = tiles.consecutiveZeros();
			//System.out.println(Arrays.toString(zeros));
			System.out.println("Total weights: " + totalWeights);
			System.out.println("Out of range weights " + tiles.countStrangeWeights());
			System.out.println("Non-zero weights: " + nonZeroWeights + " (" + nonZeroWeights / (double)totalWeights * 100 + "%)");

			agent = new AfterstateFunctionAgent<>(new TilingsSet2048VFunction(tiles, false), policy);
			break;
		default:
			throw new NotImplementedException();
		}

		Stopwatch stopwatch = Stopwatch.createStarted();
		Game2048Measurement measurement = (Game2048Measurement) performanceMeasure.measure(agent, context);
		double elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000.0;
		System.out.println(measurement);
		System.out.printf("evaluation time: %.2fs\n", elapsed);

		//OthelloLeague.saveInOthelloLeagueFormat(nTuples, new File(ntuplesFile.getAbsoluteFile() + ".txt"), State2048.BOARD_SIZE);
	}

	public static void main(String[] args) throws IllegalAccessException, InstantiationException {
		ExperimentRunner.main(args);
	}
}
