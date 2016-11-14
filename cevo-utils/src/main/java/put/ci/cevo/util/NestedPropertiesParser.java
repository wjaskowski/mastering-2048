package put.ci.cevo.util;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.stripStart;

import java.util.List;

import com.google.common.collect.Lists;

public class NestedPropertiesParser {

	/** Splits the line on the given delimiter, provided it's not located inside curly braces. */
	public static List<String> split(String line, char delimiter) {
		List<String> items = Lists.newArrayList();
		StringBuilder builder = new StringBuilder();
		boolean open = false;
		int openedTimes = 0;
		for (char c : line.toCharArray()) {
			if (c == '{') {
				builder.append(c);
				open = true;
				openedTimes++;
			} else if (c == '}') {
				builder.append(c);
				openedTimes--;
				if (openedTimes == 0) {
					open = false;
				}
			} else if (c == delimiter && !open) {
				items.add(builder.toString());
				builder.setLength(0);
			} else {
				builder.append(c);
			}
		}
		return items;
	}

	public static String parse(String string) {
		StringBuilder builder = new StringBuilder();
		boolean open = false;
		int openedTimes = 0;
		for (char c : string.toCharArray()) {
			if (c == '{') {
				builder.append(c);
				open = true;
				openedTimes++;
			} else if (c == '}') {
				builder.append(c);
				openedTimes--;
				if (openedTimes == 0) {
					open = false;
				}
			} else if (c != ';') {
				builder.append(c);
			} else {
				if (open) {
					builder.append(c);
				} else {
					builder.append(',');
				}
			}
		}

		return stripEnd(stripStart(builder.toString().trim(), ","), ",");
	}

	public static void main(String[] args) {
		// "class=TestBean3; testBean1=${class=TestBean1; x=1; name=testProperty}; a=1; b=2", ","));
		System.out.println(NestedPropertiesParser
			.parse("class=TestBean3; testBean1=${class=TestBean1; x=1; name=testProperty}; a=1; b=2"));
//		System.out.println(NestedPropertiesParser.split(
//			"class=TestBean3, testBean1=${class=TestBean1, x=1, name=testProperty}, a=1, b=2", ','));
	}
}
