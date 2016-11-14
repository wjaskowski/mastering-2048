package put.ci.cevo.experiments.rl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.random.RandomDataGenerator;

import put.ci.cevo.framework.GenotypePhenotypeMapper;
import put.ci.cevo.framework.interactions.InteractionDomain;
import put.ci.cevo.framework.interactions.InteractionResult;
import put.ci.cevo.rl.agent.Agent;
import put.ci.cevo.rl.environment.Action;
import put.ci.cevo.rl.environment.Environment;
import put.ci.cevo.rl.environment.State;
import put.ci.cevo.util.annotations.AccessedViaReflection;

public class MDPGenotypeMappingInteraction<ST extends State, A extends Action, S, T> implements InteractionDomain<S, T> {

	private class EnvironmentDirectMapping implements GenotypePhenotypeMapper<T, List<Environment<ST, A>>> {
		@Override
		public List<Environment<ST, A>> getPhenotype(T genotype, RandomDataGenerator random) {
			return Collections.singletonList((Environment<ST, A>) genotype);
		}
	}

	private final GenotypePhenotypeMapper<S, Agent<ST, A>> agentMapper;
	private final GenotypePhenotypeMapper<T, List<Environment<ST, A>>> environmentMapper;

	private final InteractionDomain<Agent<ST, A>, Environment<ST, A>> interaction;

	public MDPGenotypeMappingInteraction(GenotypePhenotypeMapper<S, Agent<ST, A>> agentMapper,
			InteractionDomain<Agent<ST, A>, Environment<ST, A>> interaction) {
		this.agentMapper = agentMapper;
		this.interaction = interaction;
		this.environmentMapper = new EnvironmentDirectMapping();
	}

	@AccessedViaReflection
	public MDPGenotypeMappingInteraction(GenotypePhenotypeMapper<S, Agent<ST, A>> agentMapper,
			GenotypePhenotypeMapper<T, List<Environment<ST, A>>> environmentMapper,
			InteractionDomain<Agent<ST, A>, Environment<ST, A>> interaction) {
		this.agentMapper = agentMapper;
		this.environmentMapper = environmentMapper;
		this.interaction = interaction;
	}

	@Override
	public InteractionResult interact(S candidate, T test, RandomDataGenerator random) {
		Agent<ST, A> agent = agentMapper.getPhenotype(candidate, random);
		List<Environment<ST, A>> environments = environmentMapper.getPhenotype(test, random);

		List<InteractionResult> results = new ArrayList<>();
		for (Environment<ST, A> env : environments) {
			results.add(interaction.interact(agent, env, random));
		}

		return InteractionResult.aggregate(results);
	}

}
