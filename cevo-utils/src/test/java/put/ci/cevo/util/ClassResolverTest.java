package put.ci.cevo.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.LocallyConfiguredCallable;
import put.ci.cevo.util.injector.TestBean1;
import put.ci.cevo.util.lister.ClassesLister;
import put.ci.cevo.util.sequence.Sequence;

public class ClassResolverTest {

	public static class InnerTestClass {
		// for classes lister test only
	}

	private static final ClassResolver resolver = ClassResolver.DefaultSingleton();
	private static final Configuration configuration = Configuration.getConfiguration();

	@Test
	public void testResolveClasses() {
		assertEquals(ClassResolverTest.class, resolver.resolve("ClassResolverTest"));
		assertEquals(ClassesLister.class, resolver.resolve("ClassesLister"));
		assertEquals(Configuration.class, resolver.resolve("Configuration"));
		assertEquals(Sequence.class, resolver.resolve("Sequence"));
		assertEquals(InnerTestClass.class, resolver.resolve("InnerTestClass"));
		assertEquals(ArrayList.class, resolver.resolve("ArrayList"));
	}

	@Test
	public void testConfigurableClass() {
		final ConfKey key = new ConfKey("test.class");
		assertEquals(TestBean1.class, new LocallyConfiguredCallable<Class<?>>() {
			@Override
			protected Class<?> callInternal() throws Exception {
				return configuration.getClass(key); // uses class resolver internally
			}
		}.withOverriddenConfiguration(key, "TestBean1").callWrapExceptions());
	}
}
