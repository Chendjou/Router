package com.urix.router.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Path {
	String route();
	String name() default "default";
	String httpMethod() default "GET";
}
