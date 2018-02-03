package io.dreiklang.breadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static io.dreiklang.breadcast.annotation.ThreadModus.POSTING;

/**
 * Annotates a method to be called by Breadcast when the respective broadcast action occurs.
 * Object must be previously registered under the generated and initialized static Breadcast instance.
 *
 * @author Nhu Huy Le, mail@huy-le.de
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Receive {

    ThreadModus threadMode() default POSTING;

    String action();

}
