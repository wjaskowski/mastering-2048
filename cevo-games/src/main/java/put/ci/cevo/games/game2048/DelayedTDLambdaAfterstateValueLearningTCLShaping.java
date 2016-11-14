package put.ci.cevo.games.game2048;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.log4j.Logger;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.evaluation.LearnableStateValueFunction;
import put.ci.cevo.rl.learn.DelayedTDLambdaAfterstateValueLearningTCL;
import put.ci.cevo.rl.learn.VFunctionLearningAlgorithm;
import put.ci.cevo.util.RandomUtils;

public class DelayedTDLambdaAfterstateValueLearningTCLShaping
		implements VFunctionLearningAlgorithm<State2048, Action2048> {

	private static final Logger logger = Logger.getLogger(DelayedTDLambdaAfterstateValueLearningTCLShaping.class);

	private final DelayedTDLambdaAfterstateValueLearningTCL<State2048, Action2048> alg;
	private final int episodesPerStage;

	private int stage;
	private int len;
	private int episodes;

	public DelayedTDLambdaAfterstateValueLearningTCLShaping(
			DelayedTDLambdaAfterstateValueLearningTCL<State2048, Action2048> alg, int episodesPerStage) {
		this.alg = alg;
		this.stage = 15;
		this.len = 15;
		this.episodes = episodesPerStage;
		this.episodesPerStage = episodesPerStage;
	}

	public long learnFromEpisode(Environment<State2048, Action2048> model,
			LearnableStateValueFunction<State2048> afterstateVFunction,
			Agent<State2048, Action2048> agent, RandomDataGenerator random) {

		if (episodes == episodesPerStage) {

		}

		State2048 initialState = getInitialState(random, model, stage, len);
		long actions = alg.learnFromEpisode(model, afterstateVFunction, agent, random, initialState);

		episodes -= 1;
		if (episodes == 0) {
			episodes = episodesPerStage;
			len -= 1;
			if (len == 0) {
				stage -= 1;
				len = stage;
			}
			logger.info("Starting shaping stage: " + stage + ", " + len);
		}
		if (len < 0) len = 0;
		if (stage < 0) stage = 0;
		return actions;
	}

	private State2048 getInitialState(RandomDataGenerator random, Environment<State2048, Action2048> model, int stage,
			int len) {
		if (stage <= 11) {
			return model.sampleInitialStateDistribution(random);
		} else {
			double[] board = new double[16];
			int[] addresses = new int[] { 0, 1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 15, 14, 13, 12 };
			for (int i = 0; i < len; ++i) {
				board[addresses[i]] = stage - i;
			}
			int randomOne = RandomUtils.nextInt(len, 15, random);
			board[addresses[randomOne]] = 1;
			return new State2048(board);
		}
	}
}
