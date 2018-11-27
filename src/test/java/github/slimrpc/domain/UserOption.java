package github.slimrpc.domain;

/**
 * UserOption
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-06 10:32
 **/
public class UserOption {

    private String name ;

    private String age ;

    //must need
    public UserOption() {
    }

    public UserOption(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
