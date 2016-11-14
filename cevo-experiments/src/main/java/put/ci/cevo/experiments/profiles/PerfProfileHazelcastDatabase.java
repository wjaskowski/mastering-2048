package put.ci.cevo.experiments.profiles;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase.Bucket;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase.PerfProfileDatabaseBuilder;
import put.ci.cevo.framework.hazelcast.HazelCastFactory;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISet;

public class PerfProfileHazelcastDatabase<T> {

	private static final HazelcastInstance hazelcast = HazelCastFactory.getInstance();
	private static final Logger logger = Logger.getLogger(PerfProfileHazelcastDatabase.class);

	private final List<ISet<EvaluatedIndividual<T>>> db;

	private final int buckets;
	private final int maxBucketSize;

	public PerfProfileHazelcastDatabase(int buckets, int maxBucketSize) {
		this.buckets = buckets;
		this.maxBucketSize = maxBucketSize;
		this.db = createDatabase(buckets);
	}

	static private <T> List<ISet<EvaluatedIndividual<T>>> createDatabase(int buckets) {
		List<ISet<EvaluatedIndividual<T>>> database = new ArrayList<ISet<EvaluatedIndividual<T>>>(buckets);
		for (int i = 0; i < buckets; ++i) {
			database.add(hazelcast.<EvaluatedIndividual<T>> getSet(Integer.toString(i)));
		}
		return database;
	}

	public int countTotalIndividuals() {
		int totalInds = 0;
		for (ISet<EvaluatedIndividual<T>> set : db) {
			totalInds += set.size();
		}
		return totalInds;
	}

	/** No lock on hazelcastDB.bucket, so we might excess the size in unfortunate circumstances */
	public boolean add(EvaluatedIndividual<T> individual) {
		int bucket = (int) (buckets * individual.getFitness());
		if (bucket == buckets) {
			bucket--;
		}

		if (db.get(bucket).size() < maxBucketSize) {
			return db.get(bucket).add(individual);
		}
		return false;
	}

	public PerfProfileHazelcastDatabase<T> merge(PerfProfileDatabase<T> other) {
		logger.info("Merging with database: " + other);
		for (Bucket<T> bucket : other) {
			for (EvaluatedIndividual<T> individual : bucket.getIndividuals()) {
				add(individual);
			}
		}
		return this;
	}

	public PerfProfileDatabase<T> toPerfProfileDatabase() {
		PerfProfileDatabaseBuilder<T> builder = new PerfProfileDatabaseBuilder<>();
		for (int idx = 0; idx < db.size(); ++idx) {
			builder.setBucket(idx, db.get(idx));
		}
		return builder.buildPerfProfileDatabase();
	}

}
