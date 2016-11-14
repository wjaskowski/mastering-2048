package put.ci.cevo.framework.individuals.loaders;

import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public abstract class SerializedFileIndividualLoader<T> implements FileIndividualLoader<T> {

	protected final SerializationManager manager;

	public SerializedFileIndividualLoader() {
		this(SerializationManagerFactory.create());
	}

	public SerializedFileIndividualLoader(SerializationManager manager) {
		this.manager = manager;
	}

}
