package put.ci.cevo.games.tetris;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import put.ci.cevo.games.tetris.agents.*;
import put.ci.cevo.rl.agent.Agent;


@SuppressWarnings("unused")
public class SZTetrisRegressionTest {

	private Tetris tetris;
	private RandomDataGenerator random;

	@Before
	public void setUp() throws Exception {
		tetris = Tetris.newSZTetris();
		random = new RandomDataGenerator(new MersenneTwister(123));
	}

	@Test
	public void regression1Test() {
		Agent<TetrisState, TetrisAction> tetrisAgent = new GECCO2015BestTDL4x4NTupleSZTetrisAgent().create();
		double reward = tetris.runEpisode(tetrisAgent, random);
		Assert.assertEquals(265.0, reward, 1e-10);
	}

	@Test
	public void regression2Test() {
		Agent<TetrisState, TetrisAction> tetrisAgent = new SzitaSzepesvariSZTetrisAgent().create();
		double reward = tetris.runEpisode(tetrisAgent, random);
		Assert.assertEquals(216.0, reward, 1e-10);
	}

	@Test
	public void regression3Test() {
		Agent<TetrisState, TetrisAction> tetrisAgent = new GECCO2015BestBICMAESSZTetrisAgent().create();
		double reward = tetris.runEpisode(tetrisAgent, random);
		Assert.assertEquals(189.0, reward, 1e-10);
	}

	@Test
	public void regression4Test() {
		Agent<TetrisState, TetrisAction> tetrisAgent = new GECCO2015BestCMAESVD3x3NTupleSZTetrisAgent().create();
		double reward = tetris.runEpisode(tetrisAgent, random);
		Assert.assertEquals(256.0, reward, 1e-10);
	}

	@Test
	public void regressionGECCO2015BestBICEMSZTetrisAgentTest() {
		Agent<TetrisState, TetrisAction> tetrisAgent = new GECCO2015BestBICEMSZTetrisAgent().create();
		double reward = tetris.runEpisode(tetrisAgent, random);
		Assert.assertEquals(175.0, reward, 1e-10);
	}
}