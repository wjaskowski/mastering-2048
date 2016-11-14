package put.ci.cevo.util.serialization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Used for binary serialization
 */
public class BinarySerializationOutput implements SerializationOutput {

	private final ObjectOutputStream output;

	public BinarySerializationOutput(FileOutputStream output) throws IOException, SerializationException {
		this(new GZIPOutputStream(output));
	}

	public BinarySerializationOutput(OutputStream output) throws IOException {
		this.output = new ObjectOutputStream(output);
	}

	@Override
	public void writeInt(int value) throws IOException {
		output.writeInt(value);
	}

	@Override
	public void writeIntArray(int[] values) throws IOException {
		output.writeObject(values);
	}

	@Override
	public void writeLong(long value) throws IOException {
		output.writeLong(value);
	}

	@Override
	public void writeString(String value) throws IOException {
		output.writeUTF(value);
	}

	@Override
	public void writeDouble(double value) throws IOException {
		output.writeDouble(value);
	}

	@Override
	public void writeDoubleArray(double[] values) throws IOException {
		output.writeObject(values);
	}

	@Override
	public void writeFloatArray(float[] values) throws IOException {
		output.writeObject(values);
	}

	@Override
	public void close() throws IOException {
		output.close();
	}
}
