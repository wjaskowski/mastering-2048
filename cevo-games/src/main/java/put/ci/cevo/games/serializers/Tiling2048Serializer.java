package put.ci.cevo.games.serializers;

import java.io.IOException;

import put.ci.cevo.games.encodings.ntuple.NTuple;
import put.ci.cevo.games.game2048.Tiling2048;
import put.ci.cevo.util.ArrayUtils;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public final class Tiling2048Serializer implements ObjectSerializer<Tiling2048> {

	@Override
	public void save(SerializationManager manager, Tiling2048 object, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeInt(object.getNumTileValues());
		output.writeInt(object.getNumSegments());
		output.writeInt(object.getMaxSegment());
		manager.serialize(object.getLocations(), output);
		manager.serialize(object.getLUT(), output);
	}

	@Override
	public Tiling2048 load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int numValues = input.readInt();
		int numSegments = input.readInt();
		int maxSegment = input.readInt();
		int[] locations = manager.deserialize(input);
		Object object = manager.deserialize(input);
		Class<?> componentType = object.getClass().getComponentType();
		if (double.class.isAssignableFrom(componentType)) {
			// Allow for reading the old format
			return new Tiling2048(locations, ArrayUtils.toFloatArray((double[]) object), numValues, numSegments, maxSegment);
		} else if (float.class.isAssignableFrom(componentType)) {
			return new Tiling2048(locations, (float[]) object, numValues, numSegments, maxSegment);
		}
		throw new SerializationException("Its neither double[] nor float[]. So what is it?");
	}

	@Override
	public int getUniqueSerializerId() {
		return 2146341566;
	}
}
