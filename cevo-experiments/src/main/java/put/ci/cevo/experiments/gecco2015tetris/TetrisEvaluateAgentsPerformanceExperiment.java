package put.ci.cevo.experiments.gecco2015tetris;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import put.ci.cevo.experiments.tetris.TetrisExpectedUtility;
import put.ci.cevo.games.tetris.*;
import put.ci.cevo.games.tetris.agents.*;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.util.random.ThreadedContext;

public class TetrisEvaluateAgentsPerformanceExperiment implements Experiment {

	static final int NUM_GAMES = 10000;

	@Override
	public void run(String[] args) {
		//saveAnimationForAgent(new SzitaSzepesvariSZTetrisAgent().create(), "tmp/hand-crafted", new ThreadedContext(126, 1));
		//saveAnimationForAgent(new GECCO2015BestTDL4x4NTupleSZTetrisAgent().create(), "tmp/tdl-4x4", new ThreadedContext(126, 1));

		evaluateAgent(new SzitaSzepesvariSZTetrisAgent()::create, SzitaSzepesvariSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
		evaluateAgent(new GECCO2015BestTDL4x4NTupleSZTetrisAgent()::create, GECCO2015BestTDL4x4NTupleSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
		evaluateAgent(new GECCO2015BestTDL3x3NTupleSZTetrisAgent()::create, GECCO2015BestTDL3x3NTupleSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
		evaluateAgent(new GECCO2015BestCMAESVD3x3NTupleSZTetrisAgent()::create, GECCO2015BestCMAESVD3x3NTupleSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
		evaluateAgent(new GECCO2015BestBICMAESSZTetrisAgent()::create, GECCO2015BestBICMAESSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
		evaluateAgent(new GECCO2015BestBICEMSZTetrisAgent()::create, GECCO2015BestBICEMSZTetrisAgent.class.getSimpleName(), NUM_GAMES);
	}

	@SuppressWarnings("unused")
	private void saveAnimationForAgent(Agent<TetrisState, TetrisAction> agent, String dir, ThreadedContext context) {
		boolean success = new File(dir).mkdirs();
		Tetris tetris = Tetris.newSZTetris();
		final int[] move = { 0 };
		final boolean[] wasReward = { false };
		final int[] totalReward = { 0 };
		tetris.runEpisode(agent, context.getRandomForThread(), transition -> {
			if (wasReward[0]) {
				try (FileWriter fileWriter = new FileWriter(new File(String.format(dir + "/move-%03da.txt",
						move[0])))) {
					fileWriter.write(transition.getState().getBoard().toString());
					fileWriter.write("Total reward = " + totalReward[0]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			totalReward[0] += transition.getReward();
			try (FileWriter fileWriter = new FileWriter(new File(String.format(dir + "/move-%03db.txt",
					move[0])))) {
				TetrisBoard middleState = transition.getState().getBoard().clone();
				middleState.placeTetrominoCustom(transition.getState().getTetromino(),
						transition.getAction().getPosition(),
						transition.getAction().getRotation(), 8);
				fileWriter.write(middleState.toString());
				fileWriter.write("Total reward = " + totalReward[0]);
			} catch (IOException e) {
				e.printStackTrace();
			}
			wasReward[0] = (transition.getReward() > 0);
			move[0] += 1;
		});
	}

	public void evaluateAgent(Supplier<Agent<TetrisState, TetrisAction>> agentSupplier, String agentName, int numGames) {
		ThreadedContext context = new ThreadedContext(1, 4);
		TetrisExpectedUtility meu = new TetrisExpectedUtility(Tetris.newSZTetris(), numGames);
		StatisticalSummary stats = meu.measure(agentSupplier, context).stats();
		System.out.println(agentName + "\n------------------------");
		System.out.print(stats);
		System.out.println("0.95 conf interval delta: " + 1.96 * stats.getStandardDeviation() / Math.sqrt(stats.getN()));
		System.out.println();
	}

	public static void main(String[] args) {
		new TetrisEvaluateAgentsPerformanceExperiment().run(args);
	}
}
