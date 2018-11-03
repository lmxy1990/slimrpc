package github.slimrpc.core.domain;

/**
 * ProviderConfig
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-02 11:53
 **/
public class ProviderHolder {
    //接口
    public Class<?> clazz ;
    //实现类
    public Object refBean ;


    public ProviderHolder(Class<?> clazz, Object refBean) {
        this.clazz = clazz;
        this.refBean = refBean;
    }
}
