package put.ci.cevo.experiments.rl;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import put.ci.cevo.util.random.ThreadedRandom;
import put.ci.cevo.rl.Simulation;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;

public class ConfiguredRLExperiment<S extends State, A extends Action> implements Runnable {

	public static enum Config implements ConfigurationKey {
		EXPERIMENT_NAME("experiment.name"),
		EXPERIMENT_SEED("experiment.seed"),
		EXPERIMENT_TARGET("experiment.target"),
		EXPERIMENT_AGENT("experiment.agent"),
		EXPERIMENT_LEARNING_ENVS("experiment.learning_env"),
		EXPERIMENT_TESTING_ENVS("experiment.testing_env"),
		EXPERIMENT_INTERACTION_EVALUATOR("experiment.interaction_evaluator");

		private final String key;

		private Config(String key) {
			this.key = key;
		}

		@Override
		public ConfigurationKey dot(Object subKey) {
			return ConfKey.dot(this, subKey);
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static final Logger logger = Logger.getLogger(ConfiguredRLExperiment.class);
	private static final Configuration configuration = Configuration.getConfiguration();

	private static final int SINGLE_RUN_ID = 0;

	private final Agent<S, A> agent;
	private final List<Environment<S, A>> learningEnvironments;
	private final List<Environment<S, A>> testingEnvironments;

	private final String name;

	private final int seed;
	private final long uniqueId;

	private Date startTime;
	private Date finishTime;
	private Object interactionEvaluator;

	public ConfiguredRLExperiment() {
		this(SINGLE_RUN_ID);
	}

	public ConfiguredRLExperiment(long uniqueId) {
		this.uniqueId = uniqueId;
		this.name = configuration.getString(Config.EXPERIMENT_NAME);
		this.seed = configuration.getSeed(Config.EXPERIMENT_SEED);
		this.agent = configuration.getObject(Config.EXPERIMENT_AGENT);
		this.learningEnvironments = configuration.createConfiguredObjects(Config.EXPERIMENT_LEARNING_ENVS);
		this.testingEnvironments = configuration.createConfiguredObjects(Config.EXPERIMENT_TESTING_ENVS);
		// this.interactionEvaluator = configuration.createConfiguredObject(Config.EXPERIMENT_INTERACTION_EVALUATOR);

		System.out.println(agent);
	}

	@Override
	public void run() {
		logger.info("Starting the experiment, run-id: " + uniqueId);

		startTime = new Date();
		ThreadedRandom random = new ThreadedRandom(seed);

		Simulation<S, A> sim = new Simulation<>(agent, learningEnvironments.get(0));
		S state = sim.run(random.forThread());
		logger.info(state);
		finishTime = new Date();

		logger.info("Finished the experiment");
	}

	public String getName() {
		return name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getFinishTime() {
		return finishTime;
	}

	public int getSeed() {
		return seed;
	}

	public long getUniqueId() {
		return uniqueId;
	}

	public static <S extends State, A extends Action> void main(String[] args) {
		ConfiguredRLExperiment<S, A> exp = new ConfiguredRLExperiment<S, A>();
		exp.run();
	}
}
