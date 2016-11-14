package put.ci.cevo.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConversionException;

/**
 * Time units in the physical sense. Generally used to describe time intervals. Based on <a
 * href="http://en.wikipedia.org/wiki/Unit_of_time">Wikipedia</a>.
 * 
 * @see IntervalConverter#convert(String)
 */
public enum TimeUnit {
	MILLIS(1l, "ms", "milliseconds", "msecs"),
	SECONDS(1000l, "s", "seconds"),
	MINUTES(60l * SECONDS.getMillis(), "min", "minutes"),
	HOURS(60l * MINUTES.getMillis(), "h", "hours"),
	DAYS(24l * HOURS.getMillis(), "d", "Days"),
	WEEKS(7l * DAYS.getMillis(), "w", "Weeks"),

	YEARS(365l * DAYS.getMillis() + DAYS.getMillis() / 4l, "a", "Years"),
	MONTHS(YEARS.getMillis() / 12l, "mth", "Months");

	private final long millis;
	private final String shortName;
	private final List<String> names;

	private TimeUnit(long multi, String shortName, String... names) {
		this.millis = multi;
		this.shortName = shortName;
		this.names = Arrays.asList(names);
	}

	public long getMillis() {
		return millis;
	}

	public String getShortName() {
		return shortName;
	}

	public List<String> getNames() {
		return names;
	}

	public boolean isPrefix(String pref, boolean ignoreCase) {
		String lowerPref = pref.toLowerCase();
		for (String name : names) {
			if (name.startsWith(pref)) {
				return true;
			}
			if (ignoreCase && name.toLowerCase().startsWith(lowerPref)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return the amount of time in the current unit converted to the given target unit.
	 */
	public double convert(double amount, TimeUnit targetUnit) {
		return amount * this.getMillis() / targetUnit.getMillis();
	}

	public static TimeUnit parse(String unit) throws ConversionException {
		List<TimeUnit> matches = new ArrayList<TimeUnit>(1);
		for (TimeUnit iUnit : values()) {
			if (unit.equals(iUnit.getShortName())) {
				matches.add(iUnit);
			} else if (iUnit.isPrefix(unit, false)) {
				matches.add(iUnit);
			}
		}
		if (matches.size() == 1) {
			return matches.get(0);
		}
		matches.clear();
		for (TimeUnit iUnit : values()) {
			if (iUnit.isPrefix(unit, true)) {
				matches.add(iUnit);
			}
		}
		if (matches.size() == 1) {
			return matches.get(0);
		}
		throw new ConversionException("Cannot convert '" + unit + "' to unit!");
	}

}