package put.ci.cevo.util.sequence.aggregates;

public interface Aggregate<S, T> {

	public S aggregate(S accumulator, T element);

}
