package put.ci.cevo.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections15.Factory;
import org.apache.commons.math3.random.RandomDataGenerator;

public class Lists {
	private Lists() {
	}

	/**
	 * Creates a list of size {@code length}, which elements are created by a random factory
	 */
	public static <T> List<T> fromFactory(int size, RandomFactory<T> factory, RandomDataGenerator random) {
		List<T> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(factory.create(random));
		}
		return list;
	}

	/**
	 * Creates a list of size {@code length}, which elements are created by a factory
	 */
	public static <T> List<T> fromFactory(int size, Factory<T> factory) {
		List<T> list = new ArrayList<>(size);
		for (int i = 0; i < size; ++i) {
			list.add(factory.create());
		}
		return list;
	}

	public static <T> List<T> reversed(List<T> list) {
		List<T> newList = new ArrayList<>(list);
		Collections.reverse(newList);
		return newList;
	}
}
