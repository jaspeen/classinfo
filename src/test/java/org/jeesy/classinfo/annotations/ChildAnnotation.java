package org.jeesy.classinfo.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Artem Mironov
 */
@Documented
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR })
@Retention(RUNTIME)
@PropertyAnnotation(requiredValue = 2)
public @interface ChildAnnotation {
}
