package put.ci.cevo.experiments.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import put.ci.cevo.experiments.profiles.PerfProfileDatabase;
import put.ci.cevo.experiments.profiles.PerfProfileDatabase.PerfProfileDatabaseBuilder;
import put.ci.cevo.experiments.profiles.PerfProfileDatabaseSerializer;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.mock.MyMockClass;

import com.google.common.collect.ImmutableList;

public class PerfProfileDatabaseSerializerTest {

	private final SerializationManager manager = SerializationManagerFactory.create();

	@Test
	public void test() throws Exception {
		PerfProfileDatabaseSerializer<MyMockClass> serializer = new PerfProfileDatabaseSerializer<MyMockClass>();

		PerfProfileDatabaseBuilder<MyMockClass> builder = new PerfProfileDatabaseBuilder<MyMockClass>();
		builder.setBucket(0, Arrays.asList(new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(55.2, "one"), 123.2, 1, 2), new EvaluatedIndividual<MyMockClass>(new MyMockClass(
			15.1, "two"), 173.2, 5, 7), new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(75.3, "three"), 1023.2, 2, 3)));

		builder.setBucket(1, ImmutableList.of(new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(55.2, "1one"), 1023.2, 31, 2), new EvaluatedIndividual<MyMockClass>(new MyMockClass(
			15.1, "2two"), 173.2, 5, 9), new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(75.3, "3three"), 1923.2, 3, 0)));

		builder.setBucket(2, Arrays.asList(new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(95.2, "8one"), 103.2, 2, 9), new EvaluatedIndividual<MyMockClass>(new MyMockClass(
			15.1, "4two"), 193.2, 5, 9), new EvaluatedIndividual<MyMockClass>(
			new MyMockClass(75.3, "5three"), 1123.2, 3, 3)));

		builder.addIndividual(0, new EvaluatedIndividual<MyMockClass>(new MyMockClass(75.3, "6three"), 11.5, 3, 4));
		builder.addIndividual(new EvaluatedIndividual<MyMockClass>(new MyMockClass(175.3, "wat"), 1132.5, 0, 0));

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		try (SerializationOutput output = new BinarySerializationOutput(byteStream)) {
			serializer.save(manager, builder.buildPerfProfileDatabase(), output);
		}

		try (BinarySerializationInput input = new BinarySerializationInput(new ByteArrayInputStream(
			byteStream.toByteArray()))) {

			PerfProfileDatabase<MyMockClass> deserialized = serializer.load(manager, input);
			Assert.assertEquals(builder.buildPerfProfileDatabase(), deserialized);
		}
	}
}