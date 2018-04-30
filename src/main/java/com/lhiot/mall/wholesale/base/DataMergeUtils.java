package com.lhiot.mall.wholesale.base;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataMergeUtils {
    public static <T>List<T> dataMerge(List<?> mainList,List<?> subList,String mainIdName,String subIdName,Class<T> type) throws IllegalAccessException, IntrospectionException, InvocationTargetException, InstantiationException {
        List<T> resultList = new ArrayList<T>();
    	List<Map<String,Object>> mainMapList = new ArrayList<>();
        List<Map<String,Object>> subMapList = new ArrayList<>();
        if (mainList != null && mainList.size() > 0 && subList != null && subList.size() > 0 ){
            for(Object t : mainList){
                mainMapList.add(convertBeanToMap(t));
            }
            for (Object t : subList) {
                subMapList.add(convertBeanToMap(t));
            }
            for (Map<String,Object> mainMap : mainMapList){
                Object mainId = mainMap.get(mainIdName);
                for (Map<String,Object> subMap : subMapList){
                    Object subId = subMap.get(subIdName);
                    if(Objects.equals(mainId,subId)){
                        for (String key : subMap.keySet()){
                            if(!mainMap.containsKey(key)){
                                mainMap.put(key,subMap.get(key));
                            }
                        }
                        resultList.add((T)convertMap(type,mainMap));
                        break;
                    }
                }
            }
        }
        return resultList;
    }

    public static Map<String,Object> convertBeanToMap(Object bean) throws IntrospectionException,IllegalAccessException, InvocationTargetException {
        Class type = bean.getClass();
        Map<String,Object> returnMap = new HashMap<String, Object>();
        BeanInfo beanInfo = Introspector.getBeanInfo(type);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (int i = 0; i < propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();
            if (!propertyName.equals("class")) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke (bean, new Object[0]);
                if (result != null) {
                    returnMap.put(propertyName, result);
                } else {
                    returnMap.put(propertyName, "");
                }
            }
        }
        return returnMap;
    }
    
    /**
     * 将一个 Map 对象转化为一个 JavaBean
     * @param type 要转化的类型
     * @param map 包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException
     *             如果分析类属性失败
     * @throws IllegalAccessException
     *             如果实例化 JavaBean 失败
     * @throws InstantiationException
     *             如果实例化 JavaBean 失败
     * @throws InvocationTargetException
     *             如果调用属性的 setter 方法失败
     */
    public static Object convertMap(Class type, Map map)
            throws IntrospectionException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
        Object obj = type.newInstance(); // 创建 JavaBean 对象

        // 给 JavaBean 对象的属性赋值
        PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
        for (int i = 0; i< propertyDescriptors.length; i++) {
            PropertyDescriptor descriptor = propertyDescriptors[i];
            String propertyName = descriptor.getName();

            if (map.containsKey(propertyName)) {
                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                Object value = map.get(propertyName);

                Object[] args = new Object[1];
                args[0] = value;

                descriptor.getWriteMethod().invoke(obj, args);
            }
        }
        return obj;
    }
    
    /*public static void main(String[] args) throws IllegalAccessException, IntrospectionException, InvocationTargetException, InstantiationException {
        List<Order> orderGridResultList = new ArrayList<>();
        Order orderGridResult1 = new Order();
        orderGridResult1.setId(1);
        orderGridResult1.setUserId(1);
        orderGridResult1.setSettlementType("cod");
        orderGridResultList.add(orderGridResult1);
        Order orderGridResult2 = new Order();
        orderGridResult2.setId(2);
        orderGridResult2.setUserId(1);
        orderGridResult2.setSettlementType("other");
        orderGridResultList.add(orderGridResult2);
        Order orderGridResult3 = new Order();
        orderGridResult3.setId(3);
        orderGridResult3.setUserId(2);
        orderGridResult3.setSettlementType("other");
        orderGridResultList.add(orderGridResult3);
        List<User> userList = new ArrayList<>();
        User user1 = new User();
        user1.setId(1);
        user1.setNickName("lalala");
        user1.setShopName("s1");
        userList.add(user1);
        User user2 = new User();
        user2.setId(2);
        user2.setNickName("wawawa");
        user2.setShopName("s2");
        userList.add(user2);
        List<DataResult> resultList = DataMergeUtils.dataMerge(orderGridResultList,userList,"userId","id",DataResult.class);
        for(DataResult dr : resultList){
        	System.out.println(dr.toString());
        }
    }*/
}

