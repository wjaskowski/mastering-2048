package put.ci.cevo.experiments.profiles;

import java.io.IOException;
import java.util.List;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase.PerfProfileDatabaseBuilder;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@Deprecated
@AutoRegistered(defaultSerializer = false)
public class PerfProfileDatabaseSerializerOld<T> implements ObjectSerializer<PerfProfileDatabase<T>> {

	@Override
	public void save(SerializationManager manager, PerfProfileDatabase<T> db, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeInt(db.getNumBuckets());
		for (int i = 0; i < db.getNumBuckets(); ++i) {
			manager.serialize(db.getBucketPlayers(i), output);
		}
	}

	@Override
	public PerfProfileDatabase<T> load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int numBuckets = input.readInt();
		PerfProfileDatabaseBuilder<T> builder = new PerfProfileDatabaseBuilder<T>();
		for (int i = 0; i < numBuckets; ++i) {
			builder.setBucket(i, manager.<List<EvaluatedIndividual<T>>> deserialize(input));
		}
		return builder.buildPerfProfileDatabase();
	}

	@Override
	public int getUniqueSerializerId() {
		return 551234530;
	}
}
