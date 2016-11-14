package put.ci.cevo.util.serialization.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationOutput;

public class IntArraySerializerTest {

	@Test
	public void testSaveLoad() throws IOException, SerializationException {
		IntArraySerializer serializer = new IntArraySerializer();
		int[] arr = new int[] { -100, 100, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 42 };

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);

		serializer.save(null, arr, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		int[] deserialized = serializer.load(null, input);
		Assert.assertArrayEquals(arr, deserialized);
	}
}
