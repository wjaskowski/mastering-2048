package put.ci.cevo.util.serialization.mock;

import java.io.IOException;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

//TODO: Move this code to src/test/java in cevo-utils.
public class MyMockClass {

	@AutoRegistered(defaultSerializer = true)
	public static class MyMockClassSerializer implements ObjectSerializer<MyMockClass> {
		@Override
		public void save(SerializationManager manager, MyMockClass object, SerializationOutput output)
				throws IOException {
			output.writeDouble(object.getD());
			output.writeString(object.getS());
		}

		@Override
		public MyMockClass load(SerializationManager manager, SerializationInput input) throws IOException,
				SerializationException {
			return new MyMockClass(input.readDouble(), input.readString());
		}

		@Override
		public int getUniqueSerializerId() {
			return 123991850;
		}
	}

	private final double d;
	private final String s;

	public MyMockClass(double d, String s) {
		this.d = d;
		this.s = s;
	}

	public double getD() {
		return d;
	}

	public String getS() {
		return s;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MyMockClass)) {
			return false;
		}
		MyMockClass o = (MyMockClass) obj;
		return (this.d == o.getD() && this.s.equals(o.getS()));
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(d).append(s).build();
	}
}