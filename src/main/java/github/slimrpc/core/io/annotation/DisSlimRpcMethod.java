package github.slimrpc.core.io.annotation;

import java.lang.annotation.*;

/**
 * 不暴露的方法
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-03 09:56
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface DisSlimRpcMethod {
}
