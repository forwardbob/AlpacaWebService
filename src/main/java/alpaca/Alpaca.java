package alpaca;


import alpaca.beans.AlpacaBeanInfo;
import alpaca.beans.BeanWrapper;
import alpaca.conf.WebProperties;
import alpaca.http.HttpServer;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

import static alpaca.conf.WebProperties.BASE_PATH;

/**
 * Created by 腼腆的老黄 on 2017/9/27.
 */
public class Alpaca {
    private Properties properties = new Properties();
    private AlpacaBeanInfo serverConfigInfo;

    public void run(Class startupClass) {
        //读取配置文件
        try {
            File file=new File(BASE_PATH+"/conf/web.properties");
            InputStream inputStream = new FileInputStream(file);
            properties = new Properties();
            properties.load(inputStream);
            initProperties();
            //todo:拼装配置文件 WebProperties
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startServer()throws Exception {
        HttpServer httpServer=new HttpServer();
        httpServer.startServer();
    }

    public void initProperties(){
        Iterator it=properties.entrySet().iterator();
        while(it.hasNext()){
            try {
                Map.Entry entry=(Map.Entry)it.next();
                Object key = entry.getKey();
                Object value = entry.getValue();
                System.out.println(key +":"+value);
                Field field = WebProperties.class.getField(key.toString().toUpperCase());
                if (field.getType()==Integer.class){
                    field.set(null,Integer.parseInt(value.toString()));
                }else if (field.getType()==String.class){
                    field.set(null,value);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}




