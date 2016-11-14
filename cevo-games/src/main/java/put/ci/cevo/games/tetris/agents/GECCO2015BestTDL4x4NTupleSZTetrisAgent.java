package put.ci.cevo.games.tetris.agents;

import org.apache.commons.collections15.Factory;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.tetris.TetrisAction;
import put.ci.cevo.games.tetris.TetrisState;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.serialization.SerializationException;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

/**
 * The best SZ-Tetris agent with 4x4 n-tuple evaluation function learned by TD(0) submitted to GECCO 2015
 */
public class GECCO2015BestTDL4x4NTupleSZTetrisAgent implements Factory<Agent<TetrisState, TetrisAction>> {

	@Override
	public Agent<TetrisState, TetrisAction> create() {
		return new DeltaNTuplesTetrisAgent(NTUPLES);
	}

	static NTuples NTUPLES;

	static {
		try {
			NTUPLES = SerializationManagerFactory.create().deserialize(
					GECCO2015BestTDL4x4NTupleSZTetrisAgent.class.getResourceAsStream(
							"gecco2015_4x4_tdl_sztetris_agent.serialized"));
		} catch (SerializationException e) {
			throw new RuntimeException(e);
		}
	}
}
