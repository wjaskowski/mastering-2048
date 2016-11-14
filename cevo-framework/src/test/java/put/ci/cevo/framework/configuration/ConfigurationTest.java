package put.ci.cevo.framework.configuration;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.BaseConfiguration;
import org.junit.Test;

import put.ci.cevo.util.IntervalConverter;
import put.ci.cevo.util.TimeUnit;
import put.ci.cevo.util.configuration.ConfKey;
import put.ci.cevo.util.configuration.Configuration;
import put.ci.cevo.util.configuration.FrameworkConfiguration;
import put.ci.cevo.util.configuration.FrameworkConfigurationWrapper;
import put.ci.cevo.util.configuration.LocallyConfiguredThread;
import put.ci.cevo.util.sequence.Sequence;
import put.ci.cevo.util.sequence.transforms.Transform;

public class ConfigurationTest {

	private static final Configuration configuration = Configuration.getConfiguration();

	@Test
	public void testImprovedInterpolation() {
		AbstractConfiguration override = new BaseConfiguration();
		override.setProperty("a", "x=${a.x}, y=${a.y}, z=${a.z}");
		override.setProperty("a.x", "qwe");
		override.setProperty("a.y", "k=5, l=6");
		override.setProperty("a.z", "qwe, asd, zxc");
		override.setProperty("b", "${a.x}/test");

		new LocallyConfiguredThread() {
			@Override
			public void run() {
				Properties a = configuration.getProperties(new ConfKey("a"));
				assertEquals("Simple property should be interpolated", "qwe", a.get("x"));
				assertEquals("Map property should not be interpolated", "${a.y}", a.get("y"));
				assertEquals("List property should not be interpolated", "${a.z}", a.get("z"));
				assertEquals("", "qwe/test", configuration.getString(new ConfKey("b")));
			}
		}.withOverriddenConfiguration(override).execute();
	}

	@Test
	public void testKeys() {
		AbstractConfiguration abstrConfig = new BaseConfiguration();
		abstrConfig.setProperty("q", 0);
		abstrConfig.setProperty("q.a", 0);
		abstrConfig.setProperty("q.s", 0);
		abstrConfig.setProperty("q.s.z", 0);
		abstrConfig.setProperty("w.a", 0);
		abstrConfig.setProperty("w.s", 0);
		abstrConfig.setProperty("w.d", 0);
		abstrConfig.setProperty("w.d.z", 0);
		abstrConfig.setProperty("e.a.z", 0);
		abstrConfig.setProperty("e.a.x", 0);
		abstrConfig.setProperty("e.s.z", 0);

		FrameworkConfiguration config = new FrameworkConfigurationWrapper(abstrConfig);

		check(config.getKeys(), "q", "q.a", "q.s", "q.s.z", "w.a", "w.s", "w.d", "w.d.z", "e.a.z", "e.a.x", "e.s.z");
		check(config.getKeys(new ConfKey("q")), "q", "q.a", "q.s", "q.s.z");
		check(config.getKeys(new ConfKey("w")), "w.a", "w.s", "w.d", "w.d.z");
		check(config.getKeys(new ConfKey("e")), "e.a.z", "e.a.x", "e.s.z");
		check(config.getKeys(new ConfKey("q.s")), "q.s", "q.s.z");
		check(config.getKeys(new ConfKey("e.a")), "e.a.z", "e.a.x");
		check(config.getSubKeys(new ConfKey("q")), "a", "s", "s.z");
		check(config.getSubKeys(new ConfKey("e")), "a.z", "a.x", "s.z");
		check(config.getImmediateKeys(), "q", "w", "e");
		check(config.getImmediateKeys(new ConfKey("q")), "q", "q.a", "q.s");
		check(config.getImmediateKeys(new ConfKey("w")), "w.a", "w.s", "w.d");
		check(config.getImmediateKeys(new ConfKey("e")), "e.a", "e.s");
		check(config.getImmediateKeys(new ConfKey("w.d")), "w.d", "w.d.z");
		check(config.getImmediateSubKeys(), "q", "w", "e");
		check(config.getImmediateSubKeys(new ConfKey("q")), null, "a", "s");
	}

	@Test
	public void testInterval1() {
		assertInterval("2.5d", TimeUnit.MILLIS, 2.5 * 24 * 60 * 60 * 1000);
		assertInterval("2.5d", TimeUnit.HOURS, 2.5 * 24);
		assertInterval("2.5d", TimeUnit.DAYS, 2.5);
		assertInterval("2.5d", TimeUnit.MONTHS, 2.5 / 30.4375);
		assertInterval("1week", TimeUnit.DAYS, 7);
		assertInterval("1Months", TimeUnit.DAYS, 30.4375);
		assertInterval("1minute", TimeUnit.DAYS, 1.0 / 60 / 24);
		assertInterval("1e-2second", TimeUnit.SECONDS, 1e-2);
		assertInterval("1e-2sec", TimeUnit.SECONDS, 1e-2);
		assertInterval("2500ms", TimeUnit.SECONDS, 2.5);
	}

	private void assertInterval(String interval, TimeUnit unit, double expect) {
		assertEquals(expect, IntervalConverter.convert(interval, unit), 0.0001);
	}

	private void check(Sequence<?> actual, String... expected) {
		Set<String> exp = new HashSet<String>(Arrays.asList(expected));
		List<String> actList = actual.transform(new Transform<Object, String>() {
			@Override
			public String transform(Object key) {
				return key == null ? null : key.toString();
			}
		}).toList();
		assertEquals(actList.toString(), exp.size(), actList.size());
		assertEquals(exp, new HashSet<String>(actList));
	}

}
