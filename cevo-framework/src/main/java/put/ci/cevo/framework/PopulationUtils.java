package put.ci.cevo.framework;

import com.google.common.base.Preconditions;
import put.ci.cevo.framework.state.EvaluatedIndividual;

import java.util.List;

public class PopulationUtils {
	private PopulationUtils() {
	}

	public static <V> long sumarizedEffort(List<EvaluatedIndividual<V>> population) {
		Preconditions.checkNotNull(population);
		long populationEffort = 0;
		for (EvaluatedIndividual<V> individual : population) {
			populationEffort += individual.getEffort();
		}
		return populationEffort;
	}
}
