package put.ci.cevo.games.serializers;

import java.io.IOException;

import put.ci.cevo.games.encodings.ntuple.DoubleNTuples;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public final class DoubleNTuplesSerializer implements ObjectSerializer<DoubleNTuples> {

	@Override
	public void save(SerializationManager manager, DoubleNTuples object, SerializationOutput output)
			throws IOException, SerializationException {
		manager.serialize(object.first(), output);
		manager.serialize(object.second(), output);
	}

	@Override
	public DoubleNTuples load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return new DoubleNTuples(manager.<NTuples>deserialize(input), manager.<NTuples>deserialize(input));
	}

	@Override
	public int getUniqueSerializerId() {
		return 968754567;
	}
}
