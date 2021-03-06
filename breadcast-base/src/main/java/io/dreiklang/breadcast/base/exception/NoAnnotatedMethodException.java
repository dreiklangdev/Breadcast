package io.dreiklang.breadcast.base.exception;

/**
 * The attempted registering of an object to Breadcast fails because no method or supermethod is
 * annotated with io.dreiklang.breadcast.annotation.Receive.
 *
 * @author Nhu Huy Le, mail@huy-le.de
 */

public class NoAnnotatedMethodException extends RuntimeException {

    public NoAnnotatedMethodException() {
    }

    public NoAnnotatedMethodException(String s) {
        super(s);
    }

}
