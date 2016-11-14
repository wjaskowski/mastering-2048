package put.ci.cevo.games.game2048;

import java.io.ByteArrayInputStream;
import java.util.List;

import nom.tam.util.BufferedDataInputStream;
import nom.tam.util.BufferedDataOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Test;
import put.ci.cevo.games.encodings.ntuple.NTuplesLocationsGeneralSystematicSupplier;
import put.ci.cevo.games.encodings.ntuple.expanders.RotationMirrorSymmetryExpander;

public class SpecialBinarySerializerTest {

	@Test
	public void testSerializeFormat1() throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(100000);
		TilingsSet2048 tiles;
		TilingsSet2048 deserialized;

		try (BufferedDataOutputStream output = new BufferedDataOutputStream(stream)) {
			List<List<int[]>> locations = new NTuplesLocationsGeneralSystematicSupplier("11;11|1", State2048.BOARD_SIZE,
					new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE), false).get();
			int numValues = 3;
			tiles = TilingsSet2048.createWithRandomWeights(2, numValues-1, locations, numValues, -10, +100,
					new RandomDataGenerator(new MersenneTwister(123)));
			SpecialBinarySerializer.serialize(tiles, output, 1);
		}
		try (BufferedDataInputStream input = new BufferedDataInputStream(new ByteArrayInputStream(stream.toByteArray()))) {
			deserialized = SpecialBinarySerializer.deserialize(input);
		}
		Assert.assertEquals(tiles, deserialized);
	}

	@Test
	public void testSerializeFormat2() throws Exception {
		ByteArrayOutputStream stream = new ByteArrayOutputStream(100000);
		TilingsSet2048 tiles;
		TilingsSet2048 deserialized;

		try (BufferedDataOutputStream output = new BufferedDataOutputStream(stream)) {
			List<List<int[]>> locations = new NTuplesLocationsGeneralSystematicSupplier("11;11|1", State2048.BOARD_SIZE,
					new RotationMirrorSymmetryExpander(State2048.BOARD_SIZE), false).get();
			int numValues = 3;
			tiles = TilingsSet2048.createWithRandomWeights(2, numValues-1, locations, numValues, -10, +100,
					new RandomDataGenerator(new MersenneTwister(123)));
			SpecialBinarySerializer.serialize(tiles, output, 2);
		}
		try (BufferedDataInputStream input = new BufferedDataInputStream(new ByteArrayInputStream(stream.toByteArray()))) {
			deserialized = SpecialBinarySerializer.deserialize(input);
		}
		Assert.assertEquals(tiles, deserialized);
	}
}