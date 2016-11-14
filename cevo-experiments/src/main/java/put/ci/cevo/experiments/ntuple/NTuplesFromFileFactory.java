package put.ci.cevo.experiments.ntuple;

import java.io.File;

import org.apache.commons.collections15.Factory;

import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

public class NTuplesFromFileFactory implements Factory<NTuples> {

	final NTuples ntuples;

	@SuppressWarnings("unchecked")
	public NTuplesFromFileFactory(File file) {
		try {
			Object object = SerializationManagerFactory.create().deserialize(file);
			if (object instanceof EvaluatedIndividual<?>) {
				this.ntuples = ((EvaluatedIndividual<NTuples>) object).getIndividual();
			} else {
				this.ntuples = (NTuples) object;
			}
		} catch (SerializationException e) {
			throw new RuntimeException("Cound not deserialize NTuples from file", e);
		}
	}

	@Override
	public NTuples create() {
		// TODO: If NTuples was truly immutable, I could just return it
		return new NTuples(ntuples);
	}
}
