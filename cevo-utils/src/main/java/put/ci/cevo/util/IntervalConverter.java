package put.ci.cevo.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertyConverter;

public class IntervalConverter {

	private static final Pattern UNIT_PATTERN = Pattern.compile("^(.+?)\\s*([A-Za-z]+)$");

	/**
	 * The interval must be specified as a number (parsed as double) and letters specifying the interval unit
	 * (optionally separated with space). Units are:
	 * <ul>
	 * <li>Years</li>
	 * <li>Months</li>
	 * <li>Weeks</li>
	 * <li>Days</li>
	 * <li>hours</li>
	 * <li>minutes</li>
	 * <li>seconds</li>
	 * <li>milliseconds or msecs</li>
	 * </ul>
	 * It is enough to give an unambiguous prefix, so these definitions are correct:
	 * <ul>
	 * <li>2.5h</li>
	 * <li>5min</li>
	 * <li>10 D</li>
	 * <li>1 day</li>
	 * <li>2 months</li>
	 * <li>1w</li>
	 * <li>5ms</li>
	 * </ul>
	 */
	public static long convert(String interval) throws ConversionException {
		return Math.round(convert(interval, TimeUnit.MILLIS));
	}

	/**
	 * Return the amount of time in the specified unit of time.
	 * 
	 * @see #convert(String)
	 */
	public static double convert(String interval, TimeUnit targetUnit) throws ConversionException {
		if (targetUnit == null) {
			targetUnit = TimeUnit.MILLIS;
		}
		Matcher match = UNIT_PATTERN.matcher(interval);
		if (!match.find()) {
			error(interval);
		}
		double number = PropertyConverter.toDouble(match.group(1));
		TimeUnit unit = TimeUnit.parse(match.group(2));
		return unit.convert(number, targetUnit);
	}

	private static void error(String interval) {
		throw new ConversionException("The value " + interval + " can't be converted to an interval");
	}

}
