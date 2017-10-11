package alpaca.beans;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Created by 腼腆的老黄 on 2017/9/27.
 */
public class BeanWrapper {
    /**
     * @param beanInfo
     * @param k
     * @param v
     */
    public static void setBeanValue(AlpacaBeanInfo beanInfo, String k, Object v) {
        Class<?> beanClass = beanInfo.getMyClass();
        List<PropertyDescriptor> propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor :propertyDescriptors) {
            if (descriptor.getName().equals(k)) {
                try {
                    System.out.println(descriptor.getPropertyType().getName());
                    //todo:这特么的要写个类型转换工具 用继承
                    //不能这么写死
                    if (descriptor.getPropertyType().getName().equals("int")) {
                        descriptor.getWriteMethod().invoke(beanInfo.getRef(),Integer.parseInt((String)v));
                    } else {
                        descriptor.getWriteMethod().invoke(beanInfo.getRef(), v);
                    }
                    break;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
