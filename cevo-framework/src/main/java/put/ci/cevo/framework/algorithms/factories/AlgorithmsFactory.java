package put.ci.cevo.framework.algorithms.factories;

import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import put.ci.cevo.framework.algorithms.OptimizationAlgorithm;
import put.ci.cevo.util.lister.ClassesLister;

import java.util.Map;

import static put.ci.cevo.util.ReflectionUtils.invokeConstructor;

@SuppressWarnings("rawtypes")
public class AlgorithmsFactory {

	private final Map<Class<? extends OptimizationAlgorithm>, Class<? extends GenerationalAlgorithmBuilder>> builders;

	private AlgorithmsFactory() {
		this(Maps.<Class<? extends OptimizationAlgorithm>, Class<? extends GenerationalAlgorithmBuilder>> newHashMap());
	}

	private AlgorithmsFactory(
			Map<Class<? extends OptimizationAlgorithm>, Class<? extends GenerationalAlgorithmBuilder>> builders) {
		this.builders = builders;
	}

	@SuppressWarnings("unchecked")
	public <A extends GenerationalAlgorithmBuilder> A getBuilder(Class<? extends OptimizationAlgorithm> algorithmClass) {
		Class<A> builderClass = (Class<A>) builders.get(algorithmClass);
		return builderClass.cast(invokeConstructor(builderClass));
	}

	private void registerBuilder(Class<? extends GenerationalAlgorithmBuilder> builderClass,
			Class<? extends OptimizationAlgorithm> targetClass) {
		builders.put(targetClass, builderClass);
	}

	public static AlgorithmsFactory create() {
		return create("put.ci.cevo.framework.algorithms");
	}

	public static AlgorithmsFactory create(String packageName) {
		final ClassesLister classesLister = new ClassesLister.Builder(packageName).buildImmediately();
		final AlgorithmsFactory factory = new AlgorithmsFactory();
		for (Class<? extends GenerationalAlgorithmBuilder> clazz : classesLister
			.getSubtypes(GenerationalAlgorithmBuilder.class)) {
			if (clazz.isAnnotationPresent(Build.class)) {
				Build build = clazz.getAnnotation(Build.class);
				Class<? extends OptimizationAlgorithm> target = build.target();
				if (OptimizationAlgorithm.class.isAssignableFrom(target)) {
					factory.registerBuilder(clazz, target);
				}
			}
		}
		return factory;
	}

	public <S, T> GenerationalAlgorithmBuilder fromAlgorithm(OptimizationAlgorithm a) {
		// ParametrizedModel sourceModel = new ParametrizedModel(a);
		// GenerationalAlgorithmBuilder builder = getBuilder(a.getClass());
		// ParametrizedModel builderModel = new ParametrizedModel(builder);
		//
		// SetView<String> intersection = intersection(sourceModel.getParametersNames(),
		// builderModel.getParametersNames());
		// for (String parameterName : intersection) {
		// Object parameter = sourceModel.getParameter(parameterName);
		// builderModel.setParameter(parameterName, parameter);
		// }
		// return builder;
		throw new NotImplementedException();
	}
}
