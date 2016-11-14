package put.ci.cevo.experiments.reports;

import static java.lang.Integer.parseInt;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.split;
import static put.ci.cevo.experiments.ConfiguredExperiment.Config.RETROSPECTION_TASKS;
import static put.ci.cevo.util.filter.Filters.notNull;
import static put.ci.cevo.util.sequence.Sequences.flatten;
import static put.ci.cevo.util.sequence.Sequences.range;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.sequence.AsyncTransformSequence;
import put.ci.cevo.util.sequence.transforms.Transform;

public class TasksConverter {

	private static final Configuration configuration = Configuration.getConfiguration();

	private static final String SEPARATOR_PATTERN = "\\.\\.|(?: |^)-(?: |$)|,";
	private static final Pattern RANGE_PATTERN = Pattern.compile("^(.*?)\\s*(?:" + SEPARATOR_PATTERN + ")\\s*(.*?)$");

	public static List<Integer> convert(String tasks) {
		if (isEmpty(tasks)) {
			return Collections.emptyList();
		}

		tasks = tasks.trim();
		if (StringUtils.isNumeric(tasks)) {
			return singletonList(parseInt(tasks));
		}

		if (tasks.equals("*")) {
			return configuration.getImmediateSubKeys(RETROSPECTION_TASKS).transform(new Transform<String, Integer>() {
				@Override
				public Integer transform(String value) {
					return parseInt(value);
				}
			}).toList();
		}

		if (tasks.contains(";")) {
			return flatten(new AsyncTransformSequence<String, List<Integer>>(Arrays.asList(split(tasks, ";"))) {
				@Override
				protected void getNext(String subtask) {
					next(convert(subtask));
				}
			}.filter(notNull())).toImmutableList();

		}

		Matcher matcher = RANGE_PATTERN.matcher(tasks);
		if (!matcher.find()) {
			throw new RuntimeException("Unable to parse range: " + tasks);
		}
		return range(parseInt(matcher.group(1)), parseInt(matcher.group(2)) + 1).toList();
	}

	public static void main(String[] args) {
		System.out.println(convert("1..5;7"));
	}
}
