package put.ci.cevo.framework.individuals.loaders;

import put.ci.cevo.framework.individuals.loaders.filters.IdentityIndividualsFilter;
import put.ci.cevo.framework.individuals.loaders.filters.IndividualsFilter;

public class LoadersFactory {

	public static <T> FilesIndividualLoader<T> serializedIndividualPerFileLoader() {
		return DefaultIndividualsLoader.create();
	}

	public static <T> FilesIndividualLoader<T> serializedIndividualPerFileLoader(IndividualsFilter<T> filter) {
		return new DefaultIndividualsLoader<T>(EvaluatedIndividualLoader.<T>individualLoader(), filter);
	}

	public static <T> FilesIndividualLoader<T> serializedMultipleIndividualsPerFileLoader() {
		return new DefaultIndividualsLoader<T>(EvaluatedIndividualsLoader.<T>individualLoader(),
				new IdentityIndividualsFilter<T>());
	}

	public static <T> FilesIndividualLoader<T> serializedMultipleIndividualsPerFileLoader(IndividualsFilter<T> filter) {
		return new DefaultIndividualsLoader<T>(EvaluatedIndividualsLoader.<T>individualLoader(), filter);
	}

}
