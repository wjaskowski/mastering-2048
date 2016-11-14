package put.ci.cevo.framework.factories;

import org.apache.commons.math3.random.RandomDataGenerator;

//TODO: IndividualFactory is the same as RandomFactory from Utils. Maybe IndividualFactory should extend RandomFactory?
//TODO: Rather: just replace this class by RandomFactory class
// This would be good since RandomFactories can give additional general methods such as create number of elements 
public interface IndividualFactory<T> {

	public T createRandomIndividual(RandomDataGenerator random);

}
