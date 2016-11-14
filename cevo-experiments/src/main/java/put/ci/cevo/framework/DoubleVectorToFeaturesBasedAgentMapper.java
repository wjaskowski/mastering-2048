package put.ci.cevo.framework;

import org.apache.commons.math3.random.RandomDataGenerator;
import put.ci.cevo.rl.agent.AfterstateFunctionAgent;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.FeaturesExtractor;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.rl.evaluation.LinearFeaturesStateValueFunction;
import put.ci.cevo.util.vectors.DoubleVector;

public class DoubleVectorToFeaturesBasedAgentMapper<S extends State, A extends Action>
		implements GenotypePhenotypeMapper<DoubleVector, Agent<S, A>> {

	private final Environment<S, A> environment;
	private final FeaturesExtractor<S> extractor;

	public DoubleVectorToFeaturesBasedAgentMapper(Environment<S, A> environment, FeaturesExtractor<S> extractor) {
		this.environment = environment;
		this.extractor = extractor;
	}

	@Override
	public Agent<S, A> getPhenotype(DoubleVector genotype, RandomDataGenerator random) {
		return new AfterstateFunctionAgent<>(new LinearFeaturesStateValueFunction<>(extractor,
				genotype), environment);
	}
}
