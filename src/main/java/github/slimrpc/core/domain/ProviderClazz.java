package github.slimrpc.core.domain;

/**
 * ProviderClazz
 * <p>
 * 释义: 配置接口--实现类的类
 *
 * @author: xinyi.pan
 * @create: 2018-11-03 10:06
 **/
public class ProviderClazz<T> {

    public Class<T> clazz;

    public T bean;

    public ProviderClazz(Class<T> clazz, T bean) {
        this.clazz = clazz;
        this.bean = bean;
    }


}
