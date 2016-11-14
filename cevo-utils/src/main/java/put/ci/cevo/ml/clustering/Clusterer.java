package put.ci.cevo.ml.clustering;

import put.ci.cevo.ml.clustering.clusterable.Clusterable;
import put.ci.cevo.util.random.ThreadedContext;

import java.util.Collection;
import java.util.List;

public interface Clusterer<T extends Clusterable> {

	public List<? extends Cluster<T>> cluster(Collection<T> points, ThreadedContext context);

}
