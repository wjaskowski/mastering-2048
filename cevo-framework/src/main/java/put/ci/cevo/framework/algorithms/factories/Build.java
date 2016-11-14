package put.ci.cevo.framework.algorithms.factories;

import put.ci.cevo.framework.algorithms.OptimizationAlgorithm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Build {
	Class<? extends OptimizationAlgorithm> target();
}
