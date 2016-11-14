package put.ci.cevo.experiments.runs.profiles.generic;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.log4j.Logger;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.profiles.generators.StrategyGenerator;
import put.ci.cevo.framework.hazelcast.HazelCastFactory;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static put.ci.cevo.framework.hazelcast.DataStructureName.RUN_ID;

public final class PerfProfileTransitionsHazelcastCalculator<S, T> implements Runnable {

	private static final Logger logger = Logger.getLogger(PerfProfileTransitionsHazelcastCalculator.class);

	private final HazelcastInstance hazelcast = HazelCastFactory.getInstance();
	private final ThreadedContext context;

	private final long workerId;

	private final SerializationManager serializationManager = SerializationManagerFactory.create();
	private PerfProfileDatabase<T> db;

	private final boolean isMainWorker;
	private final List<IMap<Integer, SummaryStatistics>> transitivity = new ArrayList<>();

	private final int numTestPairs;

	private final int numStrategies;
	private final StrategyGenerator<S> strategyGenerator;

	private final InteractionDomain<S, T> interaction;

	private final int minPerformance;
	private final int maxPerformance;

	public PerfProfileTransitionsHazelcastCalculator(StrategyGenerator<S> strategyGenerator,
			InteractionDomain<S, T> interactionDomain, int numTestPairs, int numStrategies, String dbFile,
			ThreadedContext context) {
		this(strategyGenerator, interactionDomain, 0, 100, numTestPairs, numStrategies, dbFile, context);
	}

	public PerfProfileTransitionsHazelcastCalculator(StrategyGenerator<S> strategyGenerator,
			InteractionDomain<S, T> interactionDomain, int minPerformance, int maxPerformance, int numTestPairs,
			int numStrategies, String dbFile, ThreadedContext context) {
		this.strategyGenerator = strategyGenerator;
		interaction = interactionDomain;
		this.minPerformance = minPerformance;
		this.maxPerformance = maxPerformance;
		this.numTestPairs = numTestPairs;
		this.numStrategies = numStrategies;
		this.workerId = hazelcast.getAtomicNumber(RUN_ID.getName()).incrementAndGet();
		this.context = context;

		this.isMainWorker = (workerId == 1);

		logger.info("WorkerId = " + workerId + " isMainWorker = " + isMainWorker);

		try {
			db = serializationManager.deserialize(new File(dbFile));
			logger.info("Creating hazelcast DB");
			for (int bucket = 0; bucket < db.getNumBuckets(); bucket++) {
				transitivity.add(hazelcast.<Integer, SummaryStatistics> getMap(Integer.toString(bucket)));

				for (int bucket2 = 0; bucket2 < db.getNumBuckets(); bucket2++) {
					transitivity.get(bucket).put(bucket2, new SummaryStatistics());
				}
			}
		} catch (SerializationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		RandomDataGenerator rng = context.getRandomForThread();
		for (int bucket1 = minPerformance; bucket1 < maxPerformance; bucket1++) {
			List<EvaluatedIndividual<T>> testsBucket1 = db.getBucketPlayers(bucket1);
			if (isMainWorker) {
				logTransitivities();
			}

			for (int bucket2 = minPerformance; bucket2 < maxPerformance; bucket2++) {
				List<EvaluatedIndividual<T>> testsBucket2 = db.getBucketPlayers(bucket2);

				int numIntransitivities = 0;
				for (int pair = 0; pair < numTestPairs; pair++) {
					EvaluatedIndividual<T> test1 = RandomUtils.pickRandom(testsBucket1, rng);
					EvaluatedIndividual<T> test2 = RandomUtils.pickRandom(testsBucket2, rng);

					for (int strategy = 0; strategy < numStrategies; strategy++) {
						S s = strategyGenerator.createNext(context);
						InteractionResult result1 = interaction.interact(s, test1.getIndividual(), rng);
						InteractionResult result2 = interaction.interact(s, test2.getIndividual(), rng);

						if (result1.firstResult() > result2.firstResult()) {
							numIntransitivities++;
						}
					}
				}

				double numTries = numTestPairs * numStrategies;
				double value = (numTries - numIntransitivities) / numTries;

				transitivity.get(bucket1).lock(bucket2);
				SummaryStatistics statistics = transitivity.get(bucket1).get(bucket2);
				statistics.addValue(value);
				transitivity.get(bucket1).putAndUnlock(bucket2, statistics);
			}
		}
	}

	private void logTransitivities() {
		StringBuilder message = new StringBuilder();
		for (int bucket1 = minPerformance; bucket1 < maxPerformance; bucket1++) {
			for (int bucket2 = minPerformance; bucket2 < maxPerformance; bucket2++) {
				SummaryStatistics statistics = transitivity.get(bucket1).get(bucket2);
				message.append(statistics.getMean() + " ; ");
			}
			message.append("\n");
		}
		logger.info(message);
	}
}
