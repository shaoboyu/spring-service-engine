package com.cloud.rpc.client;

import com.cloud.rpc.api.HelloService;
import com.cloud.rpc.api.SerializeUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * @author: yushaobo
 * @create: 19-1-11
 **/
public class RPCClient {

    //Socket发送消息给服务端,并反序列化服务端返回的数据,返回给方法调用者
    public static Object send(byte[] bs)  {
        try {
            Socket socket = new Socket("127.0.0.1", 9999);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bs);
            InputStream in = socket.getInputStream();
            byte[] buf = new byte[1024];
            in.read(buf);
            Object formatDate = SerializeUtils.deSerialize(buf);
            socket.close();
            return formatDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //main方法,运行客户端
    public static void main(String[] args) {
        HelloService helloService = ProxyFactory.getInstance(HelloService.class);
        System.out.println("say:"+helloService.sayHello("zhangsan"));
        System.out.println("Person:"+helloService.getPerson("zhangsan"));

    }
}
