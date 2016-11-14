package put.ci.cevo.util.configuration;

import org.apache.commons.lang.NotImplementedException;

import put.ci.cevo.util.IntervalConverter;
import put.ci.cevo.util.TimeUtils;
import put.ci.cevo.util.annotations.UnitTime;

public class RuntimeConfigurableInterval extends RuntimeConfigurable<Long> {

	public RuntimeConfigurableInterval(ConfigurationKey key, long defaultValue) {
		this(key.toString(), key, defaultValue);
	}

	public RuntimeConfigurableInterval(ConfigurationKey key, String defaultValue) {
		this(key.toString(), key, defaultValue);
	}

	public RuntimeConfigurableInterval(String name, ConfigurationKey key, long defaultValue) {
		super(name, key, defaultValue);
	}

	public RuntimeConfigurableInterval(String name, ConfigurationKey key, String defaultValue) {
		super(name, key, IntervalConverter.convert(defaultValue));
	}

	@Override
	protected Long retrieveValue(FrameworkConfiguration configuration, ConfigurationKey key) {
		return configuration.getInterval(key, defaultValue);
	}

	@Override
	protected Object displayValue(Long value) {
//        return TimeUtils.millisToHMSMs(value);
		throw new NotImplementedException();
	}

	@UnitTime("ms")
	public long getInterval() {
		return getValue();
	}

	@UnitTime("ms")
	public int getIntInterval() {
		long value = getValue();
		if ((value < Integer.MIN_VALUE) || (value > Integer.MAX_VALUE)) {
			throw new RuntimeException("Interval out of int range: " + value);
		}
		return (int) value;
	}

	@UnitTime("s")
	public double getIntervalSeconds() {
		return TimeUtils.millisToSeconds(getValue());
	}

	@UnitTime("min")
	public double getIntervalMinutes() {
		return TimeUtils.millisToMinutes(getValue());
	}

	@UnitTime("h")
	public double getIntervalHours() {
		return TimeUtils.millisToHours(getValue());
	}
}
