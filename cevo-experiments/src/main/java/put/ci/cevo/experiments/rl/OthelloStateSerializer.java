package put.ci.cevo.experiments.rl;

import java.io.IOException;

import put.ci.cevo.games.othello.OthelloBoard;
import put.ci.cevo.games.othello.mdp.OthelloState;
import put.ci.cevo.util.serialization.ObjectSerializer;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationInput;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationOutput;
import put.ci.cevo.util.serialization.serializers.AutoRegistered;

@AutoRegistered(defaultSerializer = true)
public class OthelloStateSerializer implements ObjectSerializer<OthelloState> {

	@Override
	public void save(SerializationManager manager, OthelloState object, SerializationOutput output) throws IOException,
			SerializationException {
		manager.serialize(object.getFeatures(), output);
		manager.serialize(object.getPlayerToMove(), output);
	}

	@Override
	public OthelloState load(SerializationManager manager, SerializationInput input) throws IOException,
			SerializationException {
		OthelloBoard board = new OthelloBoard();
		double[] values = manager.deserialize(input);
		for (int row = 0; row < board.getHeight(); row++) {
			for (int col = 0; col < board.getWidth(); col++) {
				board.setValue(row, col, (int) values[row * board.getWidth() + col]);
			}
		}

		int player = manager.deserialize(input);
		return new OthelloState(board, player);
	}

	@Override
	public int getUniqueSerializerId() {
		return 20130723;
	}

}
