package put.ci.cevo.experiments.profiles;

import static put.ci.cevo.util.TextUtils.format;
import static put.ci.cevo.util.sequence.Sequences.flatten;
import static put.ci.cevo.util.sequence.Sequences.seq;
import static put.ci.cevo.util.sequence.aggregates.Aggregates.statisticsAggregate;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase.Bucket;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.filter.Filter;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.LazyMap;
import put.ci.cevo.util.sequence.transforms.Transform;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

public class PerfProfileDatabase<T> implements Iterable<Bucket<T>> {

	public static class PerfProfileDatabaseBuilder<T> {

		private final Map<Integer, Set<EvaluatedIndividual<T>>> individuals;

		private int buckets;
		private int maxBucketSize;

		public PerfProfileDatabaseBuilder() {
			this(100, Integer.MAX_VALUE);
		}

		public PerfProfileDatabaseBuilder(int buckets, int maxBucketSize) {
			this.buckets = buckets;
			this.maxBucketSize = maxBucketSize;
			this.individuals = new LazyMap<Integer, Set<EvaluatedIndividual<T>>>() {
				@Override
				protected Set<EvaluatedIndividual<T>> transform(Integer num) {
					return Sets.newLinkedHashSet();
				}
			};
		}

		public synchronized void setBucket(int bucketIdx, Iterable<EvaluatedIndividual<T>> bucket) {
			individuals.put(bucketIdx, Sets.newLinkedHashSet(bucket));
		}

		public boolean addIndividual(EvaluatedIndividual<T> individual) {
			int bucket = (int) (buckets * individual.getFitness());
			return addIndividual(bucket, individual);
		}

		public synchronized boolean addIndividual(int bucket, EvaluatedIndividual<T> individual) {
			if (individuals.get(bucket).size() < maxBucketSize) {
				return individuals.get(bucket).add(individual);
			}
			return false;
		}

		public PerfProfileDatabaseBuilder<T> withBuckets(int buckets) {
			this.buckets = buckets;
			return this;
		}

		public PerfProfileDatabaseBuilder<T> withMaxBucketSize(int maxBucketSize) {
			this.maxBucketSize = maxBucketSize;
			return this;
		}

		public PerfProfileDatabase<T> buildPerfProfileDatabase() {
			List<Bucket<T>> bucketList = seq(individuals.entrySet()).map(
				new Transform<Entry<Integer, Set<EvaluatedIndividual<T>>>, Bucket<T>>() {
					@Override
					public Bucket<T> transform(Entry<Integer, Set<EvaluatedIndividual<T>>> entry) {
						return new Bucket<>(ImmutableList.copyOf(entry.getValue()), entry.getKey());
					}
				}).toSortedList();
			return new PerfProfileDatabase<T>(bucketList);
		}

		public static <F, T> PerfProfileDatabase<T> transformPerfProfileDatabase(PerfProfileDatabase<F> db,
				final Transform<F, T> function) {
			Transform<EvaluatedIndividual<F>, EvaluatedIndividual<T>> mapper = new Transform<EvaluatedIndividual<F>, EvaluatedIndividual<T>>() {
				@Override
				public EvaluatedIndividual<T> transform(EvaluatedIndividual<F> object) {
					T ind = function.transform(object.getIndividual());
					return new EvaluatedIndividual<T>(ind, object.getFitness());

				}
			};
			PerfProfileDatabaseBuilder<T> builder = new PerfProfileDatabaseBuilder<>();
			for (Bucket<F> bucket : db) {
				builder.setBucket(bucket.bucketNo, seq(bucket.players).map(mapper).toSet());
			}
			return builder.buildPerfProfileDatabase();
		}

	}

	public static class Bucket<T> implements Comparable<Bucket<T>> {

		private final ImmutableList<EvaluatedIndividual<T>> players;
		private final int bucketNo;

		public Bucket(ImmutableList<EvaluatedIndividual<T>> players, int bucketNo) {
			this.players = players;
			this.bucketNo = bucketNo;
		}

		public List<EvaluatedIndividual<T>> getIndividuals() {
			return players;
		}

		public List<T> getPlayers() {
			return seq(getIndividuals()).map(EvaluatedIndividual.<T> toIndividual()).toList();
		}

		public int getBucketNo() {
			return bucketNo;
		}

		public SummaryStatistics getStatistics() {
			return seq(players).map(EvaluatedIndividual.<T> toFitness()).aggregate(new SummaryStatistics(),
				statisticsAggregate());
		}

		public int size() {
			return getIndividuals().size();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder().append(bucketNo).append(players).toHashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			@SuppressWarnings("unchecked")
			Bucket<T> other = (Bucket<T>) obj;
			return new EqualsBuilder().append(bucketNo, other.bucketNo).append(players, other.players).isEquals();
		}

		@Override
		public int compareTo(Bucket<T> other) {
			return Integer.compare(bucketNo, other.bucketNo);
		}

		public static <T> Transform<Bucket<T>, List<EvaluatedIndividual<T>>> toIndividuals() {
			return new Transform<Bucket<T>, List<EvaluatedIndividual<T>>>() {
				@Override
				public List<EvaluatedIndividual<T>> transform(Bucket<T> bucket) {
					return bucket.getIndividuals();
				}
			};
		}

	}

	private final ImmutableList<Bucket<T>> buckets;

	private PerfProfileDatabase(Iterable<Bucket<T>> buckets) {
		this.buckets = ImmutableList.copyOf(buckets);
	}

	public List<EvaluatedIndividual<T>> getBucketPlayers(int idx) {
		return buckets.get(idx).getIndividuals();
	}

	public Bucket<T> getBucket(int idx) {
		return buckets.get(idx);
	}

	public double getBucketPerformance(int idx) {
		return idx / (double) getNumBuckets();
	}

	public int getNumBuckets() {
		return buckets.size();
	}

	public int getTotalNumElements() {
		int cnt = 0;
		for (Bucket<T> bucket : this) {
			cnt += bucket.getIndividuals().size();
		}
		return cnt;
	}

	public Sequence<EvaluatedIndividual<T>> selectWith(Filter<Bucket<T>> filter) {
		return flatten(seq(this).select(filter).map(Bucket.<T> toIndividuals()));
	}

	public List<EvaluatedIndividual<T>> playersFromBuckets(int minBucket, int maxBucket) {
		List<EvaluatedIndividual<T>> players = new ArrayList<>();
		for (int idx = minBucket; idx < maxBucket; idx++) {
			players.addAll(getBucketPlayers(idx));
		}
		return players;
	}

	@Override
	public String toString() {
		String string = "[b=" + buckets.size() + ", s=" + getTotalNumElements() + "]; ";
		for (Bucket<T> bucket : this) {
			String perf = bucket.size() > 0 ? format(bucket.getStatistics().getMean()) + "," : "0,";
			string += "[b=" + bucket.getBucketNo() + ", perf=" + perf + " s=" + bucket.size() + "]; ";
		}
		return string;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(buckets).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		@SuppressWarnings("unchecked")
		PerfProfileDatabase<T> other = (PerfProfileDatabase<T>) obj;
		return new EqualsBuilder().append(buckets, other.buckets).isEquals();
	}

	@Override
	public Iterator<Bucket<T>> iterator() {
		return buckets.iterator();
	}

	public static <T> PerfProfileDatabase<T> fromFile(File file) throws SerializationException {
		return SerializationManagerFactory.create().deserialize(file);
	}
}
