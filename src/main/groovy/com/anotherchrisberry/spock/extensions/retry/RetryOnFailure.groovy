package com.anotherchrisberry.spock.extensions.retry

import org.spockframework.runtime.extension.ExtensionAnnotation

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Retention(RetentionPolicy.RUNTIME)
@Target([ElementType.TYPE, ElementType.METHOD])
@ExtensionAnnotation(RetrySpecExtension.class)
public @interface RetryOnFailure {

    int times() default 1;
    int delaySeconds() default 0;

}
