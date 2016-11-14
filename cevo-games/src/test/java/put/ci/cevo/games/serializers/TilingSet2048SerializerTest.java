package put.ci.cevo.games.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.encodings.ntuple.NTuplesLocationsGeneralSystematicSupplier;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;
import put.ci.cevo.games.game2048.Game2048Board;
import put.ci.cevo.games.game2048.TilingsSet2048;
import put.ci.cevo.util.serialization.*;

public class TilingSet2048SerializerTest {

	@Test
	public void testSave() throws Exception {
		RandomDataGenerator random = new RandomDataGenerator(new MersenneTwister(123));
		List<List<int[]>> locations = new NTuplesLocationsGeneralSystematicSupplier("11", Game2048Board.RECT,
				new RotationMirrorSymmetryExpander(Game2048Board.RECT), true).get();
		TilingsSet2048 tilingSet = TilingsSet2048.createWithRandomWeights(2, 3, locations, 3, -1, +1,
				new RandomDataGenerator(new MersenneTwister(123)));

		SerializationManager manager = SerializationManagerFactory.create();

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			manager.serialize(tilingSet, output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {

			TilingsSet2048 deserialized = manager.deserialize(input);
			Assert.assertEquals(tilingSet, deserialized);
		}
	}
}
