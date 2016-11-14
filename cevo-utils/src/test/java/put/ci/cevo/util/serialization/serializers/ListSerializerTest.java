package put.ci.cevo.util.serialization.serializers;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.mock.MyMockClass;

public class ListSerializerTest {

	private final SerializationManager serializationManager = SerializationManagerFactory.create();

	@Test
	public void testSaveLoad() throws Exception {
		ListSerializer<MyMockClass> serializer = new ListSerializer<MyMockClass>();
		ArrayList<MyMockClass> list = new ArrayList<MyMockClass>(Arrays.asList(new MyMockClass[] {
			new MyMockClass(12.3, "asdfasd"), new MyMockClass(34562.112, "98ujjklj") }));

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			serializer.save(serializationManager, list, output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {
			List<MyMockClass> deserialized = serializer.load(serializationManager, input);
			assertTrue(list.equals(deserialized));
		}
	}
}
