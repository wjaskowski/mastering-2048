package put.ci.cevo.util.info;

import static put.ci.cevo.util.TextUtils.countWithPercentage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LocationInfo;

import put.ci.cevo.util.Pair;
import put.ci.cevo.util.TimeUtils;

import com.google.common.base.Preconditions;

public class TextProgressInfo implements ProgressInfo {

	private static final DateFormat FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private static final float INFO_PRINT_INTERVAL = 20;
	private static final float TIME_CONSTANT = 30 * 60;
	private static final float MIN_SPEED_VAL = 10;
	private static final float DIFFERENT_THRESHOLD = 0.9f;
	private static final float MIN_TIME_SHOW_FINISH_TIME = 3600;
	private static final float MIN_TIME_TO_PRINT_FINAL_INFO = 30;

	private float infoPrintInterval = INFO_PRINT_INTERVAL;

	private long startTime;
	private long lastInfoTime;
	private long lastInfoProcessed;
	private volatile long processed;

	private final Long toProcess;
	private final String description;

	private ExpMeasure speed;
	private double lastSpeed;
	private boolean started;

	private String fqcn = TextProgressInfo.class.getName();
	private Logger indivLogger;

	public TextProgressInfo(String description) {
		this(-1, description);
	}

	public TextProgressInfo(long toProcess, String description) {
		this.toProcess = toProcess == -1 ? null : toProcess;
		this.description = description;
		reset();
	}

	private void reset() {
		processed = 0;
		lastInfoProcessed = 0;
		speed = new ExpMeasure(1000 * TIME_CONSTANT);
		started = false;
		// info();
	}

	private void initTime() {
		startTime = System.currentTimeMillis();
		lastInfoTime = startTime;
		started = true;
	}

	@Override
	public void processed() {
		processed(null);
	}

	/**
	 * Information for this object than another entity was processed.
	 */
	@Override
	public void processed(Object current) {
		if (!started) {
			initTime();
		}
		if (toProcess != null && processed >= toProcess) {
			indivLog(Level.WARN, "All entities have already been processed!");
		} else {
			processed++;
		}
		justProcessed(current);
	}

	@Override
	public void multiProcessed(long multi) {
		multiProcessed(multi, null);
	}

	/** The given number of elements have just been processed in one go. */
	public void multiProcessed(long multi, Object current) {
		Preconditions.checkArgument(multi > 0, "Multi must be positive!");
		if (toProcess != null) {
			if (processed >= toProcess) {
				indivLog(Level.WARN, "All entitues have already been processed!");
				processed = toProcess;
			} else if (processed + multi > toProcess) {
				indivLog(Level.WARN, "Trying to process more entities than there are remaining!");
				processed = toProcess;
			} else {
				processed += multi;
			}
		} else {
			processed += multi;
		}
		justProcessed(current);
	}

	private void justProcessed(Object current) {
		long time = System.currentTimeMillis();
		if (time - lastInfoTime > 1000 * infoPrintInterval || (toProcess != null && processed >= toProcess)) {
			info(current);
		}
	}

	@Override
	public void finished() {
		finished(null);
	}

	/**
	 * This should be called when the iteration is complete. It makes sense to call this only when the number of
	 * elements to process was not specified.
	 */
	public void finished(Object current) {
		if (toProcess == null || processed == 0 || toProcess != processed) {
			if (toProcess != null && toProcess != processed) {
				indivLog(Level.WARN, "Finished after " + processed + " but expected " + toProcess);
			}
			// toProcess = processed;
			info(current);
		}
		reset();
	}

	private synchronized void info(Object current) {
		long time = System.currentTimeMillis();
		long deltaProcessed = processed - lastInfoProcessed;
		double deltaT = time - lastInfoTime;
		if (deltaT > 0) {
			lastSpeed = deltaProcessed / deltaT;
			speed.update(deltaT, lastSpeed);
		}
		processInfo(time, current);
		lastInfoTime = time;
		lastInfoProcessed = processed;
	}

	protected boolean different(double val1, double val2) {
		double q = val1 / val2;
		if (q > 1) {
			q = 1 / q;
		}
		return q < DIFFERENT_THRESHOLD;
	}

	protected String speedToString(double speed) {
		String timeAmount;
		double sp = speed * 1000;
		if (sp >= MIN_SPEED_VAL) {
			timeAmount = "s";
		} else {
			sp *= 60;
			if (sp > MIN_SPEED_VAL) {
				timeAmount = "min";
			} else {
				sp *= 60;
				if (sp > MIN_SPEED_VAL) {
					timeAmount = "h";
				} else {
					sp *= 24;
					timeAmount = "D";
				}
			}
		}
		return Math.round(sp) + "/" + timeAmount;
	}

	public long getProcessed() {
		return processed;
	}

	public boolean isFinished() {
		return toProcess != null && processed == toProcess;
	}

	public void setInfoPrintInterval(float infoPrintInterval) {
		this.infoPrintInterval = infoPrintInterval;
	}

	public void setTimeConstant(float timeConstant) {
		speed.setTimeConstant(1000 * timeConstant);
	}

	protected void processInfo(long time, Object current) {
		List<Pair<String, String>> info = prepareInfo(time, current);
		int maxLen = 0;
		for (Pair<String, String> pair : info) {
			String key = pair.first();
			String value = pair.second();
			if (value != null) {
				int keyLen = key.length();
				if (keyLen > maxLen) {
					maxLen = keyLen;
				}
			}
		}
		String format = String.format("  %%-%ds : %%s", maxLen);
		for (Pair<String, String> pair : info) {
			String key = pair.first();
			String value = pair.second();
			if (value == null) {
				indivLog(key);
			} else {
				indivLog(String.format(format, key, value));
			}
		}
	}

	protected List<Pair<String, String>> prepareInfo(long time, Object current) {
		List<Pair<String, String>> info = new ArrayList<Pair<String, String>>(5);
		boolean additionalInfo = processed > 0;
		long elapsedTime = time - startTime;
		if (toProcess == null) {
			info.add(Pair.create(description + " (" + processed + " / ?)" + (additionalInfo ? ":" : ""), (String) null));
		} else {
			additionalInfo = additionalInfo
				&& (processed < toProcess || elapsedTime >= 1000 * infoPrintInterval || elapsedTime >= 1000 * MIN_TIME_TO_PRINT_FINAL_INFO);
			StringBuilder text = new StringBuilder(description + " (" + countWithPercentage(processed, toProcess) + ")");
			if (additionalInfo) {
				text.append(":");
			}
			info.add(Pair.create(text.toString(), (String) null));
		}
		if (current != null) {
			info.add(Pair.create("Current", current.toString()));
		}
		if (additionalInfo) {
			double currSpeed = speed.getValue();
			double avgSpeed = processed / (double) elapsedTime;
			StringBuilder speedStr = new StringBuilder(speedToString(currSpeed));
			if (different(avgSpeed, currSpeed)) {
				speedStr.append(" (avg: ").append(speedToString(avgSpeed));
				if (different(lastSpeed, currSpeed)) {
					speedStr.append("; curr: ").append(speedToString(lastSpeed));
				}
				speedStr.append(")");
			} else {
				if (different(lastSpeed, currSpeed)) {
					speedStr.append(" (curr: ").append(speedToString(lastSpeed)).append(")");
				}
			}
			info.add(Pair.create("Speed", speedStr.toString()));
			if (toProcess != null) {
				long leftToProcess = toProcess - processed;
				long remainingTime = (long) (leftToProcess / currSpeed);
				long totalTime = elapsedTime + remainingTime;
				Date finishDate = new Date(time + remainingTime);
				StringBuilder timeText = new StringBuilder(TimeUtils.millisToHMS(elapsedTime));
				if (processed < toProcess) {
					timeText.append(" / " + TimeUtils.millisToHMS(totalTime) + " (fin: "
						+ TimeUtils.millisToHMS(remainingTime));
					if (totalTime > 1000 * MIN_TIME_SHOW_FINISH_TIME) {
						timeText.append(" (" + FORMATTER.format(finishDate) + ")");
					}
					timeText.append(")");
				}
				info.add(Pair.create("Time", timeText.toString()));
			} else {
				info.add(Pair.create("Time", TimeUtils.millisToHMS(elapsedTime) + " / ?"));
			}
		}
		return postprocessInfo(info, additionalInfo);
	}

	/**
	 * Subclasses might override this to change the printed info. The function is allowed to change the incoming info
	 * and return it as the result.
	 */
	protected List<Pair<String, String>> postprocessInfo(List<Pair<String, String>> info,
			@SuppressWarnings("unused") boolean includeAdditionalInfo) {
		return info;
	}

	private void indivLog(String msg) {
		indivLog(Level.DEBUG, msg);
	}

	protected void indivLog(Level level, String msg) {
		if (indivLogger == null) {
			indivLogger = Logger.getLogger(getIndivLoggerClass());
		}
		indivLogger.log(fqcn, level, msg, null);
	}

	protected String getIndivLoggerClass() {
		String className = new LocationInfo(new Throwable(), fqcn).getClassName();
		if (className == null || className == LocationInfo.NA) {
			return TextProgressInfo.class.getName();
		}
		return className;
	}

	public void setFQCNClass(String fqcn) {
		this.fqcn = fqcn;
		indivLogger = null;
	}

	public ProgressInfo withInfoInterval(int infoPrintInterval) {
		setInfoPrintInterval(infoPrintInterval);
		return this;
	}

}
