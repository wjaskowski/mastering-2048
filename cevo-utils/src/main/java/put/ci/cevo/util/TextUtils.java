package put.ci.cevo.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

public class TextUtils {

	public static final Charset UTF_8 = Charset.forName("UTF-8");

	private static final String UPPERCASE = "\\p{Lu}";
	private static final String LOWERCASE = "[^\\p{Lu}]";
	private static final Pattern CAMEL_CASE = Pattern.compile("^" + UPPERCASE + "(?:" + UPPERCASE + "+|" + LOWERCASE
		+ "*)(?=" + UPPERCASE + "|$)");

	public static final String REGEXP_LETTER = "\\p{L}";
	public static final String REGEXP_UPPERCASE_LETTER = "\\p{Lu}";
	public static final String REGEXP_LOWERCASE_LETTER = "[" + REGEXP_LETTER + "&&[^" + REGEXP_UPPERCASE_LETTER + "]]";

	public static String countWithPercentage(Number count, Number total) {
		return countWithPercentage(count, total, 0);
	}

	public static String countWithPercentage(Number count, Number total, int decimalDigits) {
		String str = count + " / " + total;
		if (total.doubleValue() != 0) {
			str += " = " + asPercentage(count.doubleValue() / total.doubleValue(), decimalDigits);
		}
		return str;
	}

	public static String asPercentage(double value) {
		return asPercentage(value, 0);
	}

	public static String asPercentage(double value, int decimalDigits) {
		return format(100 * value, decimalDigits) + "%";
	}

	public static Sequence<String> formatValues(Sequence<Double> seq) {
		return seq.transform(new Transform<Double, String>() {
			@Override
			public String transform(Double value) {
				return TextUtils.format(value);
			}
		});
	}

	public static String format(double value) {
		return format(value, 4);
	}

	public static String format(double value, int digits) {
		if (digits < 0) {
			throw new IllegalArgumentException("Digits: " + digits);
		}
		if (digits == 0) {
			return Integer.toString((int) value);
		}
		return StringUtils.replace(String.format("%." + digits + "f", value), ",", ".");
	}

	public static String toSnakeCase(String camel) {
		List<String> parts = new ArrayList<String>(3);
		int ind = 0;
		while (ind < camel.length()) {
			Matcher matcher = CAMEL_CASE.matcher(camel.substring(ind));
			if (!matcher.find()) {
				throw new RuntimeException("String is not in CamelCase! ('" + camel + "')");
			}
			parts.add(matcher.group().toLowerCase());
			ind += matcher.end();
		}
		return StringUtils.join(parts, "_");
	}
}
