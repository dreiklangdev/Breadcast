package com.dreiklang.breadcast.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.dreiklang.breadcast.annotation.ThreadModus.POSTING;

/**
 * Created by Huy on 29/01/2018.
 */

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface Receive {

    ThreadModus threadMode() default POSTING;

    String action();

}
