package put.ci.cevo.framework.interactions;

import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public interface Tournament<X> {

	public EvaluatedPopulation<X> execute(List<X> players, ThreadedContext context);

}
