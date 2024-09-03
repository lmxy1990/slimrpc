package github.slimrpc.core.io.annotation;

import java.lang.annotation.*;

/**
 * EnableSlimRpc
 * <p>
 * 释义: 该接口开启RPC服务
 *
 * @author: xinyi.pan
 * @create: 2018-11-02 17:49
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface OpenSlimRpc {

}
