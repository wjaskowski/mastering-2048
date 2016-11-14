package put.ci.cevo.util.serialization;

import java.io.IOException;

public interface SerializationInput extends AutoCloseable {

	int readInt() throws IOException;

	long readLong() throws IOException;

	String readString() throws IOException;

	double readDouble() throws IOException;

	int available() throws IOException;

	int[] readIntArray() throws IOException;

	double[] readDoubleArray() throws IOException;

	float[] readFloatArray() throws IOException;
}
