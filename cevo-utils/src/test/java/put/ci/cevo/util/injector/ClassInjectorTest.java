package put.ci.cevo.util.injector;

import static org.junit.Assert.assertEquals;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Test;

import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.ExtendedCompositeConfiguration;
import put.ci.cevo.util.configuration.LocallyConfiguredThread;

public class ClassInjectorTest {

	private static final ClassInjector classInjector = new ClassInjector();

	@Test
	public void testBeanProperties() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("models.0", "@TestBean1, x=1, name=testProperty");
		new LocallyConfiguredThread() {
			@Override
			public void run() {
				TestBean1 configuredBean = classInjector.injectConstructor(new ConfKey("models.0"));
				assertEquals(1, configuredBean.getX());
				assertEquals("testProperty", configuredBean.getName());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testGlobalBeanProperties() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("global.x", 1);
		override.setProperty("global.name", "testProperty");
		override.setProperty("models.0", "@TestBean1, x=${global.x}, name=${global.name}");
		new LocallyConfiguredThread() {
			@Override
			public void run() {
				TestBean1 configuredBean = classInjector.injectConstructor(new ConfKey("models.0"));
				assertEquals(1, configuredBean.getX());
				assertEquals("testProperty", configuredBean.getName());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testReferenceBeanProperties() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("model", "class=TestBean2, testField=${class=TestType}");
		new LocallyConfiguredThread() {
			@Override
			public void run() {
				TestBean2 configuredBean = classInjector.injectConstructor(new ConfKey("model"));
				assertEquals(15, configuredBean.getTestField().getNumber());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testCombinedBeans() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("models.0", "class=TestBean1, x=1, name=testProperty");
		override.setProperty("models.1", "class=TestBean3, a=1, b=2, bean1=${models.0}");

		new LocallyConfiguredThread() {
			@Override
			public void run() {
				TestBean3 configuredBean = classInjector.injectConstructor(new ConfKey("models.1"));
				assertEquals(1, configuredBean.getA());
				assertEquals(2, configuredBean.getB());
				assertEquals(1, configuredBean.getBean1().getX());
				assertEquals("testProperty", configuredBean.getBean1().getName());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testImmutableBean() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("models.0", "class=TestBean1, x=1, name=testProperty");
		override.setProperty("model.1", "class=TestBean2, testField=${class=TestType}");
		override.setProperty("models.2", "class=ImmutableTestBean, a=1, b=2, bean2=${model.1}, bean1=${models.0}");

		new LocallyConfiguredThread() {
			@Override
			public void run() {
				ImmutableTestBean configuredBean = classInjector.injectConstructor(new ConfKey("models.2"));
				assertEquals(1, configuredBean.getA());
				assertEquals(2, configuredBean.getB());
				assertEquals(1, configuredBean.getBean1().getX());
				assertEquals("testProperty", configuredBean.getBean1().getName());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testNestedProperties() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("models.1", "class=ImmutableTestBean, a=1, b=2, bean2=${class=TestBean2; "
			+ "testField=${class=TestType}}, bean1=${class=TestBean1; x=1; name=testProperty}");
		override.setProperty("models.3", "class=NestedBean, bean1=${models.1}, "
			+ "bean3=${class=TestBean3; bean1=${class=TestBean1; x=1; name=testProperty}; a=1; b=2}");

		new LocallyConfiguredThread() {
			@Override
			public void run() {
				NestedBean nestedBean = classInjector.injectConstructor(new ConfKey("models.3"));
				assertEquals(1, nestedBean.getBean1().getA());
				assertEquals(2, nestedBean.getBean3().getB());
				assertEquals("testProperty", nestedBean.getBean3().getBean1().getName());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testInstanceInjection() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("models.0", "&TestBean1, x=1, name=testProperty");
		new LocallyConfiguredThread() {
			@Override
			public void run() {
				TestBean1 configuredBean = classInjector.injectConstructor(new ConfKey("models.0"));
				configuredBean.setX(3);
				assertEquals(3, classInjector.<TestBean1> injectConstructor(new ConfKey("models.0")).getX());
			}
		}.withOverriddenConfiguration(override).execute();
	}

	public void testCommaSeparatedNestedProperties() {
		ExtendedCompositeConfiguration override = ExtendedCompositeConfiguration.createEmpty();
		override.setProperty("models.1", "class=ImmutableTestBean, a=1, b=2, bean2=${class=TestBean2, "
			+ "testField=${class=TestType}}, bean1=${class=TestBean1, x=1, name=testProperty}");

		new LocallyConfiguredThread() {
			@Override
			public void run() {
				ImmutableTestBean nestedBean = classInjector.injectConstructor(new ConfKey("models.1"));
				assertEquals(1, nestedBean.getA());
				assertEquals(2, nestedBean.getB());
			}
		}.withOverriddenConfiguration(override).execute();
	}

}
