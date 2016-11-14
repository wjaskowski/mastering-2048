package put.ci.cevo.games.serializers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import put.ci.cevo.games.game2048.Tiling2048;
import put.ci.cevo.games.game2048.TilingsSet2048;
import put.ci.cevo.util.serialization.*;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public final class TilingsSet2048Serializer implements ObjectSerializer<TilingsSet2048> {

	@Override
	public void save(SerializationManager manager, TilingsSet2048 object, SerializationOutput output)
			throws IOException, SerializationException {
		output.writeInt(object.getTilings().size());
		for (List<Tiling2048> symmetricTilings : object.getTilings()) {
			output.writeInt(symmetricTilings.size());
			manager.serialize(symmetricTilings.get(0), output);
			for (int i = 1; i < symmetricTilings.size(); ++i) {
				output.writeIntArray(symmetricTilings.get(i).getLocations());
			}
		}
	}

	@Override
	public TilingsSet2048 load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		int tilingsCount = input.readInt();
		List<List<Tiling2048>> tilings = new ArrayList<>(tilingsCount);
		for (int i = 0; i < tilingsCount; ++i) {
			int symmetricCount = input.readInt();
			List<Tiling2048> symmetricTilings = new ArrayList<>(symmetricCount);
			Tiling2048 first = manager.deserialize(input);
			symmetricTilings.add(first);
			for (int j = 1; j < symmetricCount; ++j) {
				int[] locations = input.readIntArray();
				symmetricTilings.add(Tiling2048.createWithSharedWeights(first, locations));
			}
			tilings.add(symmetricTilings);
		}
		return new TilingsSet2048(tilings);
	}

	@Override
	public int getUniqueSerializerId() {
		return 781243523;
	}
}
