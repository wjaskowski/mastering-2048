package put.ci.cevo.framework.interactions;

import put.ci.cevo.util.random.ThreadedContext;

import java.util.List;

public interface Match<X> {

	public MatchTable<X> execute(List<X> players, ThreadedContext context);

}
