package put.ci.cevo.framework.factories;

import com.google.common.collect.ImmutableList;
import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.framework.individuals.loaders.FileIndividualLoader;
import put.ci.cevo.util.annotations.AccessedViaReflection;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static put.ci.cevo.util.RandomUtils.sample;

public class ShapedSamplePopulationFactory<T> implements PopulationFactory<T> {

	private final ImmutableList<T> shapingPool;

	@AccessedViaReflection
	public ShapedSamplePopulationFactory(FileIndividualLoader<T> loader, String file) throws IOException {
		this(loader.load(new File(file)));
	}

	public ShapedSamplePopulationFactory(Collection<T> shapingPool) {
		this.shapingPool = ImmutableList.copyOf(shapingPool);
	}

	@Override
	public List<T> createPopulation(int populationSize, RandomDataGenerator random) {
		return sample(shapingPool, populationSize, random);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

}
