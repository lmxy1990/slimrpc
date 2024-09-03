package github.slimrpc.core.io.annotation;

import java.lang.annotation.*;

/**
 * 该接口由服务端实现
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-02 17:49
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface SlimRpc {

}
