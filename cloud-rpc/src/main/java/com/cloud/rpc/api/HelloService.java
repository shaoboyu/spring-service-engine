package com.cloud.rpc.api;

/**
 * @author: yushaobo
 * @create: 19-1-11
 **/
public interface HelloService {

    String sayHello(String name);

    Person getPerson(String name);
}
