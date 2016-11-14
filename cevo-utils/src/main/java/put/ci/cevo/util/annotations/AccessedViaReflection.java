package put.ci.cevo.util.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The annotated element is accessed via reflection. This means that changing it's name, signature etc. possibly breaks
 * the code!
 */
@Documented
@Retention(SOURCE)
@Target({ ANNOTATION_TYPE, CONSTRUCTOR, FIELD, METHOD, TYPE })
public @interface AccessedViaReflection {

	/** Provides information on where the element is accessed via reflection. */
	String[] from() default {};

}
