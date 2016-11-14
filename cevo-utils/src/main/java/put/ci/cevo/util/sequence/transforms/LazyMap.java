package put.ci.cevo.util.sequence.transforms;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import put.ci.cevo.util.ReflectionUtils;

public class LazyMap<K, V> implements Map<K, V> {

	protected final Map<K, V> base;
	protected final Transform<K, V> transform;

	public LazyMap(Class<? extends V> valueClass) {
		this(valueClass, null);
	}

	/**
	 * Value is initialised with a new instance of the value class. The key is passed to the constructor if the key
	 * class is specified (not null).
	 */
	public LazyMap(final Class<? extends V> valueClass, final Class<? extends K> keyClass) {
		this(new Transform<K, V>() {
			private Constructor<? extends V> constructor;

			@Override
			public V transform(K key) {
				if (constructor == null) {
					if (keyClass == null) {
						constructor = ReflectionUtils.getMatchingConstructor(valueClass);
						if (constructor == null) {
							throw new RuntimeException("No empty constructor of " + valueClass);
						}
					} else {
						constructor = ReflectionUtils.getMatchingConstructor(valueClass, keyClass);
						if (constructor == null) {
							throw new RuntimeException("No constructor of " + valueClass + " with argument "
								+ key.getClass());
						}
					}
				}
				if (keyClass == null) {
					return ReflectionUtils.invokeConstructor(constructor);
				}
				return ReflectionUtils.invokeConstructor(constructor, key);
			}
		});
	}

	public LazyMap(Map<K, V> base, Transform<K, V> transform) {
		this.base = base;
		this.transform = transform;
	}

	public LazyMap(Transform<K, V> transform) {
		this(new HashMap<K, V>(), transform);
	}

	public LazyMap(Map<K, V> base) {
		this.base = base;
		this.transform = new Transform<K, V>() {
			@Override
			public V transform(K k) {
				return LazyMap.this.transform(k);
			}
		};
	}

	public LazyMap() {
		this(new HashMap<K, V>());
	}

	@Override
	public void clear() {
		base.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return base.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return base.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return base.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return base.equals(o);
	}

	@Override
	public V get(Object key) {
		if (base.containsKey(key)) {
			return base.get(key);
		}
		@SuppressWarnings("unchecked")
		K k = (K) key;
		V v = transform.transform(k);
		base.put(k, v);
		return v;
	}

	@Override
	public int hashCode() {
		return base.hashCode();
	}

	@Override
	public boolean isEmpty() {
		return base.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return base.keySet();
	}

	@Override
	public V put(K key, V value) {
		return base.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		base.putAll(m);
	}

	@Override
	public V remove(Object key) {
		return base.remove(key);
	}

	@Override
	public int size() {
		return base.size();
	}

	@Override
	public Collection<V> values() {
		return base.values();
	}

	protected V transform(@SuppressWarnings("unused") K k) {
		throw new RuntimeException("Must either implement transform or pass a Transform");
	}

	@Override
	public String toString() {
		return base.toString();
	}

	protected Map<K, V> getBase() {
		return base;
	}

}
