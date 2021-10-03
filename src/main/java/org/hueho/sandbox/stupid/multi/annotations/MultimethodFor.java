package org.hueho.sandbox.stupid.multi.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MultimethodFor {
    String value();

    boolean deriveMirror() default false;
}
