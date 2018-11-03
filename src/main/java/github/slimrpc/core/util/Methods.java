package github.slimrpc.core.util;

import org.springframework.util.DigestUtils;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Methods
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-02 10:14
 **/
public class Methods {


    /**
     * 获取某个类中,生成方法的唯一标识
     *
     * @param method
     * @return
     */
    public static String methodSign(Method method) {
        if (method == null) return "";
        StringBuilder builder = new StringBuilder("method:");
        String name = method.getName();
        builder.append(name);
        builder.append("_");
        int count = method.getParameterCount();
        builder.append(count);
        builder.append("_");
        if (count > 0) {
            Class<?>[] classes = method.getParameterTypes();
            Arrays.stream(classes).forEach(c -> builder.append(c.getName() + ","));
        }
        String string = builder.toString();
        String hex = DigestUtils.md5DigestAsHex(string.getBytes());
        return hex ;
    }






}
