package put.ci.cevo.experiments.profiles;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase.Bucket;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.util.random.ThreadedContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static org.apache.commons.io.FileUtils.openOutputStream;

public class PerfProfile {

	public static class PerfProfileBuilder {

		private final ProfileBucket[] buckets;

		public PerfProfileBuilder(int numBuckets) {
			buckets = new ProfileBucket[numBuckets];
		}

		public synchronized void setBucketPerformance(int bucketIdx, DescriptiveStatistics stats) {
			buckets[bucketIdx] = new ProfileBucket(stats.getMean(), stats.getStandardDeviation(), (int) stats.getN());
		}

		public PerfProfile buildPerfProfile() {
			return new PerfProfile(this);
		}

	}

	private static final class ProfileBucket {

		private final double mean;
		private final double stdev;
		private final int count;

		public ProfileBucket(double mean, double stdev, int count) {
			this.mean = mean;
			this.stdev = stdev;
			this.count = count;
		}
	}

	private final ImmutableList<ProfileBucket> buckets;

	private PerfProfile(PerfProfileBuilder builder) {
		this.buckets = ImmutableList.copyOf(builder.buckets);
	}

	public int getNumBuckets() {
		return buckets.size();
	}

	public double getBucketPerformance(int idx) {
		return idx / (double) getNumBuckets();
	}

	public void saveAsCSV(File file) throws IOException {
		saveAsCSV(openOutputStream(file));
	}

	public void saveAsCSV(FileOutputStream outputStream) {
		try (PrintWriter out = new PrintWriter(outputStream)) {
			out.println("BucketPerf,Mean,Stdev,Count");
			for (int b = 0; b < getNumBuckets(); ++b) {
				out.println(String.format("%.2f,%.5f,%.5f,%d", getBucketPerformance(b), buckets.get(b).mean,
					buckets.get(b).stdev, buckets.get(b).count));
			}
		}
	}

	/**
	 * Create a performance profile for a team of players using multiple threads
	 */
	public static <S, T> PerfProfile createForPlayerTeam(PerfProfileDatabase<T> db,
			final InteractionDomain<S, T> interaction, final List<S> players, ThreadedContext context) {
		final PerfProfileBuilder builder = new PerfProfileBuilder(db.getNumBuckets());

		// 1 task = 1 bucket
		context.submit(new ThreadedContext.Worker<Bucket<T>, Void>() {
			@Override
			public Void process(Bucket<T> bucket, ThreadedContext context) throws Exception {
				DescriptiveStatistics bucketStatistics = new DescriptiveStatistics();
				for (S player : players) {
					for (T opponent : bucket.getPlayers()) {
						double result = interaction.interact(player, opponent, context.getRandomForThread()).firstResult();
						bucketStatistics.addValue(result);
					}
				}
				builder.setBucketPerformance(bucket.getBucketNo(), bucketStatistics);
				return null;
			}
		}, db);

		return builder.buildPerfProfile();
	}

}
