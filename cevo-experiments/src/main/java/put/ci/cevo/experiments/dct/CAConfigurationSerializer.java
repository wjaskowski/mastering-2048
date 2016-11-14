package put.ci.cevo.experiments.dct;

import put.ci.cevo.games.dct.CAConfiguration;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

import java.io.IOException;

@AutoRegistered(defaultSerializer = true)
public class CAConfigurationSerializer implements ObjectSerializer<CAConfiguration> {

	@Override
	public void save(SerializationManager manager, CAConfiguration object, SerializationOutput output) throws IOException,
			SerializationException {
		output.writeIntArray(object.toArray());
		output.writeDouble(object.getDensity());
	}

	@Override
	public CAConfiguration load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		return new CAConfiguration(input.readIntArray(), input.readDouble());
	}

	@Override
	public int getUniqueSerializerId() {
		return 657876543;
	}

}
