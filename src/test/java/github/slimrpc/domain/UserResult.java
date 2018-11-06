package github.slimrpc.domain;

/**
 * UserResult
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-06 10:32
 **/
public class UserResult {

    private String name ;

    private String loginInfo ;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginInfo() {
        return loginInfo;
    }

    public void setLoginInfo(String loginInfo) {
        this.loginInfo = loginInfo;
    }
}
