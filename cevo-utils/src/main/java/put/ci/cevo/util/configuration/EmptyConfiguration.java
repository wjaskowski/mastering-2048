package put.ci.cevo.util.configuration;

import java.util.Iterator;

import org.apache.commons.configuration.AbstractConfiguration;

import com.google.common.collect.Iterators;

public class EmptyConfiguration extends AbstractConfiguration {

	@Override
	protected void addPropertyDirect(String key, Object value) {
		throw new RuntimeException("Configuration is unmodifiable!");
	}

	@Override
	public boolean containsKey(String key) {
		return false;
	}

	@Override
	public Iterator<String> getKeys() {
		return Iterators.emptyIterator();
	}

	@Override
	public Object getProperty(String key) {
		return null;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}

}
