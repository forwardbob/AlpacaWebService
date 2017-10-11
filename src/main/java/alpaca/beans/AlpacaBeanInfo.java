package alpaca.beans;

import java.beans.BeanDescriptor;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 腼腆的老黄 on 2017/9/26.
 */
public class AlpacaBeanInfo<T> {
    //方法
    private Method[] methods;
    //属性描述
    private List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
    //bean描述
    private BeanDescriptor beanDescriptor = null;
    //反射实例化的引用
    private Object ref=null;
    //class name
    private String name;
    private  Class aClass;
    public AlpacaBeanInfo(Class beanClass) {
        this.methods = beanClass.getMethods();
        this.propertyDescriptors=getAllPropertyDescriptor(beanClass,propertyDescriptors);
        this.beanDescriptor=new BeanDescriptor(beanClass);
        try {
            this.ref=beanClass.newInstance();
        }catch (Exception e){}
        this.name=beanClass.getName();

    }


    /**
     * 返回bean的属性详情
     * @param beanClass
     * @param list
     * @return
     */
    private List getAllPropertyDescriptor(Class beanClass,List list){
        Field[] fields = beanClass.getDeclaredFields();
        for (Field field:fields){
            String fieldName = field.getName();
            try {
                PropertyDescriptor descriptor = new PropertyDescriptor(fieldName, beanClass);
                list.add(descriptor);
            } catch (IntrospectionException e) {
            }

        }
        return list;
    }

    public Method[] getMethods() {
        return methods;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

    public List<PropertyDescriptor> getPropertyDescriptors() {
        return propertyDescriptors;
    }

    public void setPropertyDescriptors(List<PropertyDescriptor> propertyDescriptors) {
        this.propertyDescriptors = propertyDescriptors;
    }

    public BeanDescriptor getBeanDescriptor() {
        return beanDescriptor;
    }

    public void setBeanDescriptor(BeanDescriptor beanDescriptor) {
        this.beanDescriptor = beanDescriptor;
    }

    public T getRef() {
        return (T) ref;
    }

    public String getName() {
        return name;
    }

    public Class getMyClass() {
        return aClass;
    }
}
