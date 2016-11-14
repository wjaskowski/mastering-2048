package put.ci.cevo.util;

import static java.lang.Math.abs;
import static java.lang.Math.floor;
import static java.util.Locale.ENGLISH;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.time.DateUtils;

public class TimeUtils {

	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	public static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DATE_TIME_MILLIS_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public static final DateFormat DAY_MON_FORMAT = new SimpleDateFormat("E MMM dd H:m:s z yyyy", ENGLISH);

	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");

	public static final long MILLIS_PER_SECOND = 1000l;
	public static final long MILLIS_PER_MINUTE = MILLIS_PER_SECOND * 60;
	public static final long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
	public static final long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;
	public static final long MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;
	public static final long MILLIS_PER_MONTH = MILLIS_PER_DAY * 30;
	public static final int SECONDS_PER_MINUTE = 60;

	private static final long DST_OFFSET = TimeZone.getDefault().getOffset(System.currentTimeMillis());

	public static long parseEpochTime(String date) {
		try {
			return toEpochTime(date);
		} catch (ParseException e) {
			return -1;
		}
	}

	public static long toEpochTime(String date) throws ParseException {
		return DAY_MON_FORMAT.parse(date).getTime() / MILLIS_PER_SECOND;
	}

	public static String formatTimeHours(double hours) {
		String timeString = hours >= 0 ? "" : "-";
		double time = abs(hours);
		timeString += (int) floor(time) + "h ";
		time = (time - floor(time)) * 60.0;
		timeString += DECIMAL_FORMAT.format((int) floor(time)) + "min ";
		time = (time - floor(time)) * 60.0;
		timeString += DECIMAL_FORMAT.format((int) floor(time)) + "s";
		return timeString;
	}

	public static String formatTimeSeconds(double seconds) {
		return formatTimeHours(seconds / 3600.0);
	}

	public static String formatTimeMillis(long millis) {
		return formatTimeHours(millisToHours(millis));
	}

	public static String formatTimeChange(double hours) {
		final String timeString = formatTimeHours(hours);
		return timeString.startsWith("-") ? timeString.replaceAll("0h ", "") : "+" + timeString.replaceAll("0h ", "");
	}

	public static double millisToHours(long millis) {
		return (double) millis / MILLIS_PER_HOUR;
	}

	public static double millisToSeconds(long millis) {
		return (double) millis / MILLIS_PER_SECOND;
	}

	public static double secondsToHours(double seconds) {
		return seconds / 3600.0;
	}

	public static double millisToMinutes(long millis) {
		return (double) millis / MILLIS_PER_MINUTE;
	}

	public static long hoursToMillis(double hours) {
		return (long) (hours * MILLIS_PER_HOUR);
	}

	public static long daysToMillis(double days) {
		return (long) (days * MILLIS_PER_DAY);
	}

	public static long weeksToMillis(double weeks) {
		return (long) (weeks * MILLIS_PER_WEEK);
	}

	public static long monthsToMillis(double months) {
		return (long) (months * MILLIS_PER_MONTH);
	}

	public static long hoursToSeconds(double hours) {
		return Math.round(hours * MILLIS_PER_HOUR / 1000.0);
	}

	public static long minutesToMillis(double minutes) {
		return Math.round(minutes * MILLIS_PER_MINUTE);
	}

	public static String millisToHMS(long millis) {
		boolean minus = false;
		if (millis < 0) {
			minus = true;
			millis = -millis;
		}
		long s = millis / 1000;
		long m = s / 60;
		long h = m / 60;
		String hms = String.format("%02d:%02d:%02d", h, m % 60, s % 60);
		return minus ? "-" + hms : hms;
	}

	private static final String SHORT_FORMAT = millisToHMS(0);

	public static String millisToHMSShort(long millis) {
		boolean minus = false;
		if (millis < 0) {
			minus = true;
			millis = -millis;
		}
		String hms = millisToHMS(millis);
		int i;
		for (i = 0; i < hms.length() - 1; i++) {
			if (hms.charAt(i) != SHORT_FORMAT.charAt(i)) {
				break;
			}
		}
		String hmsShort = hms.substring(i);
		return minus ? "-" + hmsShort : hmsShort;
	}

	public static String hoursToHMS(double hours) {
		return millisToHMS(Math.round(MILLIS_PER_HOUR * hours));
	}

	public static String hoursToHMSShort(double hours) {
		return millisToHMSShort(Math.round(MILLIS_PER_HOUR * hours));
	}

	/** Returns number of days between two dates. */
	public static int getDaysBetweenDates(final Date startDate, final Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / DateUtils.MILLIS_PER_DAY);
	}

	/** Returns number of seconds between two dates. */
	public static int getSecondsBetweenDates(final Date startDate, final Date endDate) {
		return (int) ((endDate.getTime() - startDate.getTime()) / DateUtils.MILLIS_PER_SECOND);
	}

	/** Returns number of days between two dates as human see that. */
	public static int getHumanDaysBetweenDates(final Date startDate, final Date endDate) {
		Calendar endDateCal = Calendar.getInstance();
		endDateCal.setTime(endDate);
		endDateCal.set(Calendar.HOUR_OF_DAY, 0);
		endDateCal.set(Calendar.MINUTE, 0);
		endDateCal.set(Calendar.SECOND, 0);
		return (int) ((endDateCal.getTime().getTime() - startDate.getTime() + DateUtils.MILLIS_PER_DAY) / DateUtils.MILLIS_PER_DAY);
	}

	private static double dateToDoubleDays(long date) {
		return (double) (date + DST_OFFSET) / MILLIS_PER_DAY;
	}

	private static long doubleDaysToDate(double days) {
		return (long) (days * MILLIS_PER_DAY) - DST_OFFSET;
	}

	/** Number of day since epoch. */
	public static int dateToDays(long time) {
		return (int) dateToDoubleDays(time);
	}

	public static float getHour(long time) {
		double days = dateToDoubleDays(time);
		return (float) (24 * (days - Math.floor(days)));
	}

	public static long daysToDate(int day) {
		return doubleDaysToDate(day);
	}

	public static long daysToDate(int day, float hour) {
		return doubleDaysToDate(day + hour / 24.0);
	}

	/** Quickly compute day of week, between {@link Calendar#SUNDAY} and {@link Calendar#SATURDAY} */
	public static int getDayOfWeek(long time) {
		return (dateToDays(time) + 4) % 7 + 1;
	}

	public static boolean isWeekend(long time) {
		int dow = getDayOfWeek(time);
		return dow == Calendar.SATURDAY || dow == Calendar.SUNDAY;
	}

	public static Date addDays(Date date, int days) {
		return add(date, Calendar.DAY_OF_YEAR, days);
	}

	public static Date addHours(Date date, int hours) {
		return add(date, Calendar.HOUR_OF_DAY, hours);
	}

	public static Date addMinutes(Date date, int minutes) {
		return add(date, Calendar.MINUTE, minutes);
	}

	public static Date addMillis(Date date, int days) {
		return add(date, Calendar.MILLISECOND, days);
	}

	private static Date add(Date date, int unit, int value) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(unit, value);

		return calendar.getTime();
	}

	public static String dateTimeFormat(long time) {
		return dateTimeFormat(new Date(time));
	}

	public static String dateTimeFormat(Date date) {
		return DATE_TIME_FORMAT.format(date);
	}

	public static String formatIfSet(long time) {
		return time == 0 ? "not set" : TimeUtils.dateTimeFormat(time);
	}
}
