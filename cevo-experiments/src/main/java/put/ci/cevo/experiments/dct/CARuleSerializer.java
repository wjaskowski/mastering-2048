package put.ci.cevo.experiments.dct;

import java.io.IOException;

import put.ci.cevo.games.dct.CARule;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public class CARuleSerializer implements ObjectSerializer<CARule> {

	@Override
	public void save(SerializationManager manager, CARule object, SerializationOutput output) throws IOException,
			SerializationException {
		output.writeIntArray(object.toArray());
	}

	@Override
	public CARule load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return new CARule(input.readIntArray());
	}

	@Override
	public int getUniqueSerializerId() {
		return 78653123;
	}

}
