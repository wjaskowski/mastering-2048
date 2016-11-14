package put.ci.cevo.framework.retrospection.tasks;

import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.util.Describable;
import put.ci.cevo.util.random.ThreadedContext;

public interface RetrospectionTask extends Describable {

	public RetrospectionResult retrospect(Retrospector retrospector, ThreadedContext context);

}
