package com.cloud.rpc.server;

import com.cloud.rpc.api.HelloService;
import com.cloud.rpc.api.Person;

/**
 * @author: yushaobo
 * @create: 19-1-11
 **/
public class HelloServiceImpl implements HelloService {

    public String sayHello(String name) {
        return "hello,"+name;
    }

    public Person getPerson(String name) {
        Person person = new Person();
        person.setName(name);
        person.setAge(20);
        return person;
    }
}
