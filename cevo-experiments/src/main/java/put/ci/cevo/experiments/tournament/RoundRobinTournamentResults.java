package put.ci.cevo.experiments.tournament;

import static com.google.common.primitives.Doubles.asList;
import static put.ci.cevo.framework.random.bootstrap.Bootstrap.bootstrapConfidenceIntervals;
import static put.ci.cevo.util.sequence.Sequences.seq;

import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import put.ci.cevo.util.random.ThreadedContext;
import put.ci.cevo.framework.random.bootstrap.Bootstrapable;
import put.ci.cevo.util.TableUtil;
import put.ci.cevo.util.TableUtil.TableBuilder;
import put.ci.cevo.util.info.TextProgressInfo;
import put.ci.cevo.util.sequence.aggregates.Aggregate;

import com.google.common.collect.Lists;

public class RoundRobinTournamentResults<S, T> {

	private final ResultsTable<PlayersTeam<S>, PlayersTeam<T>> results;

	public RoundRobinTournamentResults(ResultsTable<PlayersTeam<S>, PlayersTeam<T>> results) {
		this.results = results;
	}

	public ResultsTable<PlayersTeam<S>, PlayersTeam<T>> getResultsTable() {
		return results;
	}

	public Bootstrapable<Double> getBootstrapableResult(final PlayersTeam<S> teamA, final PlayersTeam<T> teamB) {
		return new Bootstrapable<Double>() {
			@Override
			public List<Double> getSample() {
				return asList(results.get(teamA, teamB).getValues());
			}
		};
	}

	public String getTextTable(ThreadedContext context) {
		return getTextTable(results.teamsA().toList(), results.teamsB().toList(), context);
	}

	public String getTextTable(List<PlayersTeam<S>> teamsA, List<PlayersTeam<T>> teamsB, ThreadedContext context) {
		TableBuilder builder = new TableUtil.TableBuilder();
		builder.addHeaders("Team");
		builder.addHeaders(teamsB.toArray());
		builder.addHeaders("Total");
		TextProgressInfo info = new TextProgressInfo(
			teamsA.size() * teamsB.size(), "Bootstrapping confidence intervals");

		for (PlayersTeam<S> teamA : teamsA) {
			List<String> rowList = Lists.newArrayList();
			rowList.add(teamA.name);
			for (PlayersTeam<T> teamB : teamsB) {
				if (results.get(teamA, teamB).getN() == 0) {
					rowList.add("-");
					info.processed();
					continue;
				}
				Bootstrapable<Double> bootstrapableResult = getBootstrapableResult(teamA, teamB);
				rowList.add(bootstrapConfidenceIntervals(bootstrapableResult, 10000, 0.05, context));
				info.processed();
			}

			List<Double> avgSample = seq(results.teamAResults(teamA)).aggregate(Lists.<Double> newArrayList(),
				new Aggregate<List<Double>, DescriptiveStatistics>() {
					@Override
					public List<Double> aggregate(List<Double> accumulator, DescriptiveStatistics element) {
						accumulator.addAll(asList(element.getValues()));
						return accumulator;
					}
				});
			rowList.add(bootstrapConfidenceIntervals(avgSample, 10000, 0.05, context));
			builder.addRow(rowList);
		}
		return builder.toString();
	}

}