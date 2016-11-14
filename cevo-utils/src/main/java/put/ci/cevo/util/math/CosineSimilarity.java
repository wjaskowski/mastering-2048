package put.ci.cevo.util.math;

public class CosineSimilarity implements DistanceMetric {

	@Override
	public double distance(double[] a, double[] b) {
		double dotProduct = 0.0;
		double magnitude1 = 0.0;
		double magnitude2 = 0.0;
		double cosineSimilarity = 0.0;

		// a and b must be of same length
		for (int i = 0; i < a.length; i++) {
			dotProduct += a[i] * b[i]; // a.b
			magnitude1 += Math.pow(a[i], 2); // (a^2)
			magnitude2 += Math.pow(b[i], 2); // (b^2)
		}

		magnitude1 = Math.sqrt(magnitude1); // sqrt(a^2)
		magnitude2 = Math.sqrt(magnitude2); // sqrt(b^2)

		if (magnitude1 != 0.0 | magnitude2 != 0.0) {
			cosineSimilarity = dotProduct / (magnitude1 * magnitude2);
		} else {
			return 0.0;
		}
		return cosineSimilarity;
	}

}