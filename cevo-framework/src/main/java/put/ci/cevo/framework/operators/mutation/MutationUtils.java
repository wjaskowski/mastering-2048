package put.ci.cevo.framework.operators.mutation;

public class MutationUtils {

	public static double clamp(double weight, double lowerBound, double upperBound) {
		if (weight > upperBound) {
			weight = upperBound;
		} else if (weight < lowerBound) {
			weight = lowerBound;
		}
		return weight;
	}
}
