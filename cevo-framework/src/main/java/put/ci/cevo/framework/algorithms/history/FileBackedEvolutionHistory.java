package put.ci.cevo.framework.algorithms.history;

import com.google.common.io.FileBackedOutputStream;
import put.ci.cevo.framework.algorithms.history.policy.HistoryStoragePolicy;
import put.ci.cevo.framework.algorithms.history.policy.PersistAllPolicy;
import put.ci.cevo.framework.state.EvolutionState;
import put.ci.cevo.util.serialization.BinarySerializationInput;
import put.ci.cevo.util.serialization.BinarySerializationOutput;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManager;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Objects.toStringHelper;

public class FileBackedEvolutionHistory implements EvolutionHistory {

	private static final long serialVersionUID = -6315772729433664534L;

	private static final int DEFAULT_MEMORY_BUFFER_SIZE = 5 * 1000 * 1000;

	private final SerializationManager manager;

	private final HistoryStoragePolicy policy;
	private final FileBackedOutputStream historyStream;

	public FileBackedEvolutionHistory() {
		this(SerializationManager.createDefault(), new PersistAllPolicy(), DEFAULT_MEMORY_BUFFER_SIZE);
	}

	public FileBackedEvolutionHistory(SerializationManager manager, HistoryStoragePolicy policy, int memoryBufferSize) {
		this.manager = manager;
		this.policy = policy;
		this.historyStream = new FileBackedOutputStream(memoryBufferSize);
	}

	@Override
	public void onNextGeneration(EvolutionState state) {
		if (policy.qualifies(state)) {
			try (BinarySerializationOutput output = new BinarySerializationOutput(historyStream)) {
				manager.serialize(state, output);
			} catch (SerializationException | IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public List<EvolutionState> getEvolutionHistory() {
		try (BinarySerializationInput input = new BinarySerializationInput(historyStream.asByteSource()
			.openBufferedStream())) {
			return manager.<EvolutionState> deserializeStream(input).toList();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("size", getEvolutionHistory().size()).toString();
	}

}
