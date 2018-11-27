package github.slimrpc.service;

import github.slimrpc.domain.UserOption;
import github.slimrpc.domain.UserResult;

/**
 * UserService
 * <p>
 * 释义:
 *
 * @author: xinyi.pan
 * @create: 2018-11-06 09:39
 **/
public interface UserService {

    String sayHello(String name) ;


    UserResult login(UserOption userOption) ;

}
