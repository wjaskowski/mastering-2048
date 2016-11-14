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

public class DoubleArraySerializerTest {

	@Test
	public void testSaveLoad() throws IOException, SerializationException {
		DoubleArraySerializer serializer = new DoubleArraySerializer();
		double[] arr = new double[] { 123.4, 15666.34, 23251234.1451234233, -123123.01, Double.MAX_VALUE,
			Double.MIN_VALUE, 0 };

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		SerializationOutput output = new BinarySerializationOutput(byteStream);

		serializer.save(null, arr, output);

		BinarySerializationInput input = new BinarySerializationInput(
			new ByteArrayInputStream(byteStream.toByteArray()));

		double[] deserialized = serializer.load(null, input);
		Assert.assertArrayEquals(arr, deserialized, 0.0);
	}
}
