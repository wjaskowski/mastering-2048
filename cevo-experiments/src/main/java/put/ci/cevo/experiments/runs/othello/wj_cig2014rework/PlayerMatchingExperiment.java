package put.ci.cevo.experiments.runs.othello.wj_cig2014rework;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.shaded.org.objenesis.strategy.StdInstantiatorStrategy;
import org.apache.commons.lang3.builder.CompareToBuilder;
import put.ci.cevo.experiments.othello.OthelloInteractionDomain;
import put.ci.cevo.experiments.othello.RepeatedInteractionDomain;
import put.ci.cevo.framework.evaluators.EvaluatedPopulation;
import put.ci.cevo.framework.fitness.Fitness;
import put.ci.cevo.framework.fitness.SimpleAverageFitness;
import put.ci.cevo.framework.interactions.*;
import put.ci.cevo.framework.state.EvaluatedIndividual;
import put.ci.cevo.games.board.BoardEvaluationType;
import put.ci.cevo.games.encodings.ntuple.NTuples;
import put.ci.cevo.games.othello.DoubleOthello;
import put.ci.cevo.games.othello.LucasInitialOthelloStates;
import put.ci.cevo.games.othello.players.OthelloNTuplesPlayer;
import put.ci.cevo.games.othello.players.OthelloPlayer;
import put.ci.cevo.newexperiments.Experiment;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.util.serialization.SerializationManager;
import put.ci.cevo.util.serialization.SerializationManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

import static put.ci.cevo.util.sequence.Sequences.seq;

public class PlayerMatchingExperiment implements Experiment {

	private static final Configuration config = Configuration.getConfiguration();
	private final int THREADS = config.getInt(new ConfKey("threads"), 4);
	private final int SEED = config.getInt(new ConfKey("seed"), 101);

	private ThreadedContext context = new ThreadedContext(SEED, THREADS);
	private final SerializationManager serializer = SerializationManagerFactory.create();
	private final OthelloInteractionDomain domain = new OthelloInteractionDomain(new DoubleOthello(),
			new LucasInitialOthelloStates().boards());

	@Override
	public void run(String[] args) {
		Kryo kryo = new Kryo();
		kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());

		/*List<OthelloPlayer> players = readPlayers();
		players = players.subList(0, 100);
		System.out.println("Population = " + players.size());
		List<EvaluatedIndividual<OthelloPlayer>> evaluated = evaluatedPerformance(players);

		try (Output output = new Output(new FileOutputStream(new File("evaluated.bin")))) {
			kryo.writeObject(output, evaluated);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}*/

		ArrayList<EvaluatedIndividual<OthelloPlayer>> perfect;

		try (Input input = new Input(new FileInputStream(new File("evaluated.bin")))) {
			perfect = kryo.readObject(input, ArrayList.class);
			sortByEvaluation(perfect);
		} catch (FileNotFoundException e) {
			throw new RuntimeException();
		}

		List<OthelloPlayer> myPlayers = seq(perfect).map(EvaluatedIndividual.<OthelloPlayer>toIndividual()).toList();

		for (int k = 1; k < 100; k += 5) {
			List<EvaluatedIndividual<OthelloPlayer>> kRandomOpponentsEvaluated = swissEvaluation(myPlayers, k);
			System.out.println(evaluationError(perfect, kRandomOpponentsEvaluated));
		}

		/*for (int i = 0; i < perfect.size(); ++i) {
			System.out.println(perfect.get(i).getIndividual().hashCode());
		}
		System.out.println();
		for (int i = 0; i < perfect.size(); ++i) {
			System.out.println(kRandomOpponentsEvaluated.get(i).getIndividual().hashCode());
		}*/
	}

	// Assuming both are sorted
	private double evaluationError(final List<EvaluatedIndividual<OthelloPlayer>> perfect,
			final List<EvaluatedIndividual<OthelloPlayer>> evaluated) {

		ArrayList<EvaluatedIndividual<OthelloPlayer>> clone = new ArrayList<>(evaluated);
		int badlyOderedPairs = 0;
		int n = perfect.size();

		for (int i = 0; i < n; ++i) {
			int index = -1;
			for (int j = i; j < n; ++j) {
				if (clone.get(j).getIndividual() == perfect.get(i).getIndividual()) {
					index = j;
					break;
				}
			}
			badlyOderedPairs += (index - i);
			//System.out.println(" >" + (index - i));
			EvaluatedIndividual<OthelloPlayer> removed = clone.remove(index);
			clone.add(0, removed);
		}

		// A reverse has error 1.0
		return badlyOderedPairs / (0.5 * n * (n - 1));
	}

	private List<EvaluatedIndividual<OthelloPlayer>> kRandomEvaluation(List<OthelloPlayer> players, int k) {
		KRandomOpponentsTournament<OthelloPlayer> tournament = new KRandomOpponentsTournament<>(domain, k,
				KRandomOpponentsMatch.OpponentsStrategy.RANDOM_OPPONENTS);
		EvaluatedPopulation<OthelloPlayer> population = tournament.execute(players, context);
		List<EvaluatedIndividual<OthelloPlayer>> evaluated = population.getPopulation();
		sortByEvaluation(evaluated);
		return evaluated;
	}

	private List<EvaluatedIndividual<OthelloPlayer>> kFixedEvaluation(List<OthelloPlayer> players, int k) {
		KRandomOpponentsTournament<OthelloPlayer> tournament = new KRandomOpponentsTournament<>(domain, k,
				KRandomOpponentsMatch.OpponentsStrategy.FIXED_OPPONENTS);
		EvaluatedPopulation<OthelloPlayer> population = tournament.execute(players, context);
		List<EvaluatedIndividual<OthelloPlayer>> evaluated = population.getPopulation();
		sortByEvaluation(evaluated);
		return evaluated;
	}

	private List<EvaluatedIndividual<OthelloPlayer>> swissEvaluation(List<OthelloPlayer> players, int k) {
		SwissTournament<OthelloPlayer> tournament = new SwissTournament<>(domain, k,
				SwissTournament.SwissStrategy.SECONDARY_POINTS);
		EvaluatedPopulation<OthelloPlayer> population = tournament.execute(players, context);
		List<EvaluatedIndividual<OthelloPlayer>> evaluated = population.getPopulation();
		sortByEvaluation(evaluated);
		return evaluated;
	}

	private List<EvaluatedIndividual<OthelloPlayer>> danishEvaluation(List<OthelloPlayer> players, int k) {
		DanishTournament<OthelloPlayer> tournament = new DanishTournament<>(domain, k);
		EvaluatedPopulation<OthelloPlayer> population = tournament.execute(players, context);
		List<EvaluatedIndividual<OthelloPlayer>> evaluated = population.getPopulation();
		sortByEvaluation(evaluated);
		return evaluated;
	}

	private List<EvaluatedIndividual<OthelloPlayer>> kRandomOpponentsEvaluation(List<OthelloPlayer> players, int k) {
		InteractionTable<OthelloPlayer, OthelloPlayer> table = new KRandomOpponentsInteractionScheme<>(
				domain, k).interact(players, players, context);
		Map<OthelloPlayer, Fitness> fitnessMap = new SimpleAverageFitness().aggregateFitness(
				table.getSolutionsPayoffs(), context);
		ArrayList<EvaluatedIndividual<OthelloPlayer>> evaluated = new ArrayList<>();
		for (OthelloPlayer player : players) {
			evaluated.add(new EvaluatedIndividual<>(player, fitnessMap.get(player)));
		}
		sortByEvaluation(evaluated);
		return evaluated;
	}

	private List<EvaluatedIndividual<OthelloPlayer>> evaluatedPerformance(List<OthelloPlayer> players) {
		final int REPEATS = 10;
		InteractionDomain<OthelloPlayer, OthelloPlayer> repeated = new RepeatedInteractionDomain<>(domain, REPEATS);
		InteractionTable<OthelloPlayer, OthelloPlayer> table = new OnePopulationRoundRobinAlternativeInteractionScheme<>(
				repeated).interact(players, players, context);
		Map<OthelloPlayer, Fitness> fitnessMap = new SimpleAverageFitness().aggregateFitness(
				table.getSolutionsPayoffs(), context);
		ArrayList<EvaluatedIndividual<OthelloPlayer>> evaluated = new ArrayList<>();
		for (OthelloPlayer player : players) {
			evaluated.add(new EvaluatedIndividual<>(player, fitnessMap.get(player)));
		}
		sortByEvaluation(evaluated);
		return evaluated;
	}

	public void sortByEvaluation(List<EvaluatedIndividual<OthelloPlayer>> players) {
		Collections.sort(players, new Comparator<EvaluatedIndividual<OthelloPlayer>>() {
			@Override
			public int compare(EvaluatedIndividual<OthelloPlayer> o1, EvaluatedIndividual<OthelloPlayer> o2) {
				return -new CompareToBuilder().append(o1, o2).toComparison();
			}
		});
	}

	public List<OthelloPlayer> readPlayers() {
		List<NTuples> ntuples = serializer.deserializeWrapExceptions(new File("pop.dump"));

		List<OthelloPlayer> players = new ArrayList<>();

		for (NTuples ntuple : ntuples) {
			players.add(new OthelloNTuplesPlayer(ntuple, BoardEvaluationType.BOARD_INVERSION));
		}
		return players;
	}

	public static void main(String[] args) {
		new PlayerMatchingExperiment().run(args);
	}
}
