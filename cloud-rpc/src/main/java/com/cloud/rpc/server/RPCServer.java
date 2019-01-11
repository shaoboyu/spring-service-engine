package com.cloud.rpc.server;

import com.cloud.rpc.api.NetModel;
import com.cloud.rpc.api.SerializeUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author: yushaobo
 * @create: 19-1-11
 **/
public class RPCServer {
    public static void main(String[] args)
    {
        try {
            openServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //这个方法用来启动服务端,然后接受数据,返回处理完的结果
    public static void openServer() throws IOException {
        ServerSocket serverSocket =  new ServerSocket(9999);
        try {
            System.out.println("服务开启");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println(socket.getInetAddress()+"-connected");
                InputStream in = socket.getInputStream();
                byte[] buf = new byte[1024];
                in.read(buf);
                byte[] formatDate = formatData(buf);
                OutputStream out = socket.getOutputStream();
                out.write(formatDate);
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            serverSocket.close();
        }
    }
    /*这个方法用来处理接收到的数据,通过反序列化得到传过来的通信类NetModel,然后得到接口名,方法名,参数,参数类型,
    最后通过JDK反射,来调取实现类的方法,并将调取结果,序列化为byte数组,然后返回
    */
    public static byte[] formatData(byte[] bs){
        try {
            //将收到的byte数组反序列化为NetModel类型,然后通过反射调用HelloServiceImpl实现类的方法
            NetModel netModel = (NetModel) SerializeUtils.deSerialize(bs);
            String className = netModel.getClassName();
            String[] types = netModel.getTypes();
            Object[] args = netModel.getArgs();

//            这里简单通过Map来做接口映射到实现类,从map中取
            Map<String, String> map = new HashMap<String,String>();
            map.put("com.cloud.rpc.api.HelloService", "com.cloud.rpc.server.HelloServiceImpl");
            Class<?> clazz = Class.forName(map.get(className));

//            //也可以把这个键值放到配置文件下，通过配置文件读取
//            Class<?> clazz = Class.forName(getPropertyValue(className));
            Class<?> [] typeClazzs = null;
            if(types!=null) {
                typeClazzs = new Class[types.length];
                for (int i = 0; i < typeClazzs.length; i++) {
                    typeClazzs[i] = Class.forName(types[i]);
                }
            }
            Method method = clazz.getMethod(netModel.getMethod(),typeClazzs);
            Object object = method.invoke(clazz.newInstance(), args);
            byte[] byteArray = SerializeUtils.serialize(object);
            return byteArray;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //通过key去properties文件中取值
    public static String getPropertyValue(String key) throws IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("src/main/resources/config.properties");
        properties.load(in);
        in.close();
        return properties.getProperty(key);
    }
}

