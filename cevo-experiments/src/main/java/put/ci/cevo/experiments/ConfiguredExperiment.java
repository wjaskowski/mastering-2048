package put.ci.cevo.experiments;

import org.apache.commons.lang.ObjectUtils;
import org.apache.log4j.Logger;
import put.ci.cevo.framework.retrospection.RetrospectionResult;
import put.ci.cevo.framework.retrospection.RetrospectionTable;
import put.ci.cevo.framework.retrospection.Retrospector;
import put.ci.cevo.framework.retrospection.tasks.RetrospectionTask;
import put.ci.cevo.framework.termination.EvolutionTarget;
import put.ci.cevo.util.Describable;
import put.ci.cevo.util.Description;
import put.ci.cevo.util.HostnameUtil;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.ConfigurationKey;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.random.ThreadedRandom;

import java.util.Date;
import java.util.List;

import static java.lang.Runtime.getRuntime;
import static java.lang.System.getenv;
import static org.apache.commons.lang.StringUtils.join;
import static put.ci.cevo.experiments.ConfiguredExperiment.Config.*;
import static put.ci.cevo.util.TimeUtils.formatTimeMillis;
import static put.ci.cevo.util.sequence.Sequences.seq;

public class ConfiguredExperiment implements Runnable, Describable {

	public static enum Config implements ConfigurationKey {
		EXPERIMENT_ID("experiment.id"),
		EXPERIMENT_NAME("experiment.name"),
		EXPERIMENT_SEED("experiment.seed"),
		EXPERIMENT_THREADS("experiment.threads"),
		EXPERIMENT_TARGET("experiment.target"),

		EXPERIMENT_ALGORITHMS("experiment.model"),
		EXPERIMENT_QUERY("experiment.query"),
		EXPERIMENT_CONTEXT("experiment.context"),
		RETROSPECTION_TASKS("experiment.retrospectionTask");

		private final String key;

		private Config(String key) {
			this.key = key;
		}

		public ConfigurationKey dot(Object subKey) {
			return ConfKey.dot(this, subKey);
		}

		@Override
		public String toString() {
			return key;
		}
	}

	private static final Logger logger = Logger.getLogger(ConfiguredExperiment.class);
	private static final Configuration configuration = Configuration.getConfiguration();

	private final List<Model> models;
	private final List<RetrospectionTask> tasks;

	private final EvolutionTarget target;
	private final RetrospectionTable outcomeTable;

	private final String name;

	private final int threads;
	private final int seed;
	private final long uniqueId;

	private Date startTime;
	private Date finishTime;

	public ConfiguredExperiment() {
		this(configuration.getInt(EXPERIMENT_ID));
	}

	public ConfiguredExperiment(long uniqueId) {
		this.uniqueId = uniqueId;
		this.name = configuration.getString(EXPERIMENT_NAME);
		this.seed = configuration.getSeed(EXPERIMENT_SEED);
		this.threads = configuration.getInt(EXPERIMENT_THREADS, getRuntime().availableProcessors());
		this.models = loadAlgorithms(EXPERIMENT_ALGORITHMS);
		this.target = getTarget(EXPERIMENT_TARGET);
		this.tasks = getTasks(RETROSPECTION_TASKS);
		this.outcomeTable = RetrospectionTable.create(tasks.size(), models.size());

	}

	private List<RetrospectionTask> getTasks(ConfigurationKey key) {
		if (!configuration.getSubKeys(key).toList().isEmpty()) {
			return configuration.createConfiguredObjects(key);
		}
		throw new RuntimeException("Retrospection task not specified!");
	}

	private List<Model> loadAlgorithms(ConfigurationKey key) {
		return configuration.createConfiguredObjects(key);
	}

	private EvolutionTarget getTarget(ConfigurationKey key) {
		if (configuration.containsKey(key)) {
			return configuration.getObject(key);
		}
		throw new RuntimeException("Evolution target not defined!");
	}

	@Override
	public void run() {
		logger.info("Starting the experiment, run-id: " + uniqueId);
		logger.info("Models: " + models);

		startTime = new Date();
		for (Model model : seq(models).info("Evaluating models")) {
			logger.info("Evolving model: " + model);
			ThreadedRandom random = new ThreadedRandom(seed, threads);
			Retrospector retrospector = model.evolve(target, random, threads);
			logger.info("Retrospection started for model: " + model);
			retrospect(model, retrospector, random);
		}

		finishTime = new Date();
		logger.info("Finished the experiment");
	}

	protected void retrospect(Model algorithm, Retrospector retrospector, ThreadedRandom random) {
		for (RetrospectionTask task : tasks) {
			RetrospectionResult outcome = task.retrospect(retrospector, new ThreadedContext(random, threads));
			outcomeTable.put(task.describe().getName(), algorithm.getName(), outcome);
		}
	}

	public RetrospectionTable getRetrospectionTable() {
		return outcomeTable;
	}

	public List<Model> getModels() {
		return models;
	}

	public List<RetrospectionTask> getTasks() {
		return tasks;
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

	public long getUniqueId() {
		return uniqueId;
	}

	@Override
	public Description describe() {
		Description description = new Description();

		description.addSection("host");
		description.addProperty("name", HostnameUtil.getHostName());
		description.addProperty("address", HostnameUtil.getHostAddress());
		description.addProperty("os", System.getProperty("os.name", "UNKNOWN"));
		description.addProperty("processor.id", ObjectUtils.toString(getenv("PROCESSOR_IDENTIFIER"), "UNKNOWN"));
		description.addProperty("processor.arch", ObjectUtils.toString(getenv("PROCESSOR_ARCHITECTURE"), "UNKNOWN"));
		description.addProperty("processor.cores", getRuntime().availableProcessors());

		description.addSection("experiment");
		description.addProperties(this, "name", "startTime", "finishTime");
		description.addProperty("seed", seed);
		description.addProperty("runningTime", formatTimeMillis(finishTime.getTime() - startTime.getTime()));

		description.addSection("experiment.algorithms");
		for (Model model : models) {
			description.addProperty(model.getName(), model);
		}

		description.addSection("experiment.tasks");
		for (RetrospectionTask task : tasks) {
			description.addProperty(task);
		}

		description.addSection("experiment.targets");
		description.addDescription(target.describe());

		description.addSection("experiment.configuration");
		for (ConfigurationKey key : configuration.getKeys()) {
			description.addProperty(key.toString(), join(configuration.getList(key), ", "));
		}
		return description;
	}

}
