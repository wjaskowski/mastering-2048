package put.ci.cevo.experiments.shaping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.vectors.DoubleVector;
import put.ci.cevo.util.RandomUtils;
import put.ci.cevo.util.annotations.AccessedViaReflection;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class PerfProfileInteractionDomain<S, T> implements InteractionDomain<S, DoubleVector> {

	private static final SerializationManager serializationManager = SerializationManagerFactory.create();

	private InteractionDomain<S, T> innerInteractionDomain;
	private PerfProfileDatabase<T> db;

	@AccessedViaReflection
	public PerfProfileInteractionDomain(String dbFile, InteractionDomain<S, T> interactionDomain) {
		this.innerInteractionDomain = interactionDomain;

		try {
			db = serializationManager.deserialize(new File(dbFile));
		} catch (SerializationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InteractionResult interact(S candidate, DoubleVector opponent, RandomDataGenerator random) {
		List<InteractionResult> results = new ArrayList<>();
		for (double difficulty : opponent.toArray()) {
			int numBucket = (int) (difficulty * db.getNumBuckets());
			List<EvaluatedIndividual<T>> bucket = db.getBucketPlayers(Math.min(numBucket, db.getNumBuckets() - 1));
			EvaluatedIndividual<T> evaluatedIndividual = RandomUtils.pickRandom(bucket, random);

			results.add(innerInteractionDomain.interact(candidate, evaluatedIndividual.getIndividual(), random));
		}

		return InteractionResult.aggregate(results);
	}
}
