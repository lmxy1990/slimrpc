package com.github.slimrpc.fastjson;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
public @interface GenericsResult {
	Class returnType();
}
