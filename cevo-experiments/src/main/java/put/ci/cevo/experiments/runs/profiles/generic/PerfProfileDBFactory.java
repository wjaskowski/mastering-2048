package put.ci.cevo.experiments.runs.profiles.generic;

import com.google.common.base.Preconditions;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.framework.factories.PopulationFactory;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PerfProfileDBFactory<T> implements PopulationFactory<T> {

	private static final SerializationManager serializationManager = SerializationManagerFactory.create();
	private final List<T> pool;

	@AccessedViaReflection
	public PerfProfileDBFactory(String dbFile, int maxPerformance, int minPerformance) {
		this.pool = readTestsOfDifficulty(dbFile, minPerformance, maxPerformance);
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		Preconditions.checkArgument(populationSize <= pool.size());
		return RandomUtils.sample(pool, populationSize, random);
	}

	public static <T> List<T> readTestsOfDifficulty(String dbFile, int minPerformance, int maxPerformance) {
		List<T> pool = new ArrayList<>();
		try {
			PerfProfileDatabase<T> db = serializationManager.deserialize(new File(dbFile));
			for (int performance = minPerformance; performance < maxPerformance && performance < db.getNumBuckets(); performance++) {
				List<EvaluatedIndividual<T>> bucket = db.getBucketPlayers(performance);
				for (EvaluatedIndividual<T> evaluatedWPC : bucket) {
					pool.add(evaluatedWPC.getIndividual());
				}
			}
		} catch (SerializationException e) {
			throw new RuntimeException("Could not deserialize performance profile database", e);
		}
		return pool;
	}
}
