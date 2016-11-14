package put.ci.cevo.framework.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.mock.MyMockClass;

public class EvaluatedIndividualSerializerTest {

	private final SerializationManager serializationManager = SerializationManagerFactory.create();

	@Test
	public void testSaveLoad() throws Exception {
		EvaluatedIndividualSerializer<MyMockClass> serializer = new EvaluatedIndividualSerializer<MyMockClass>();

		EvaluatedIndividual<MyMockClass> ind = new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(1.2, "asdfad"), 123.2, 12, 1412);

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			serializer.save(serializationManager, ind, output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {

			EvaluatedIndividual<MyMockClass> deserialized = serializer.load(serializationManager, input);
			Assert.assertEquals(ind, deserialized);
		}
	}
}