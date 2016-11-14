package put.ci.cevo.framework.fitness;

public interface Fitness extends Comparable<Fitness> {

	public double fitness();
	
	public Fitness negate();
	
	public boolean betterThan(Fitness other);
	
}
