package com.lhiot.mall.wholesale.base;

import com.leon.microx.util.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class DataMergeUtils {

    public static <T> List<T> dataMerge(List<?> list1, List<?> list2, String mainId, String subId, Class<T> resultType) {
        if (CollectionUtils.isEmpty(list1)) {
            return new ArrayList<>(0);
        }
        if (CollectionUtils.isEmpty(list2)) {
            return list1.parallelStream().map(BeanUtils::toMap).map(map -> {
                try {
                    T instance = resultType.newInstance();
                    BeanUtils.populate(instance).from(map);
                    return instance;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }).collect(Collectors.toList());
        }

        List<T> resultList = new ArrayList<>();
        list1.stream().map(BeanUtils::toMap).forEach(map1 -> {
            Object mainIdValue = map1.get(mainId);
            if (Objects.isNull(mainIdValue)) {
                T instance = newInstanceFromMap(resultType, map1);
                resultList.add(instance);
            } else {
                boolean subIsNull = true;
                for (Object o : list2) {
                    Map<String, Object> map2 = BeanUtils.toMap(o);
                    if (Objects.equals(mainIdValue, map2.get(subId))) {
                        Map<String, Object> mergeMap = new HashMap<>(map2);
                        //mergeMap.putAll(map1);
                        //合并两个集合的元素
                        for (Map.Entry<String, Object> entry : map1.entrySet()) {
                            String key = entry.getKey();
                            Object value = entry.getValue();
                            //如果主集合中元素的键不存在或值为空时，将副集合元素中的键值插入或覆盖
                            if(!mergeMap.containsKey(key) || (mergeMap.containsKey(key) && Objects.isNull(mergeMap.get(key)))){
                                mergeMap.put(key,value);
                            }
                        }
                        T instance = newInstanceFromMap(resultType, mergeMap);
                        resultList.add(instance);
                        subIsNull = false;
                        break;
                    }
                }
                if(subIsNull){//主List元素在副List中找不到关联值时，返回主List元素
                    T instance = newInstanceFromMap(resultType, map1);
                    resultList.add(instance);
                }
            }
        });
        return resultList;
    }

    private static <T> T newInstanceFromMap(Class<T> type, Map<String, Object> map) {
        try {
            T instance = type.newInstance();
            BeanUtils.populate(instance).from(map);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*public static Map<?, ?> objectToMap(Object obj) {
        if(obj == null)
            return null;

        return new org.apache.commons.beanutils.BeanMap(obj);
    }*/

//    public static <T> List<T> merge(List<?> mainList, List<?> subList, String mainIdName, String subIdName, Class<T> type) throws IntrospectionException, IllegalAccessException, InvocationTargetException, InstantiationException {
//        List<T> resultList = new ArrayList<T>();
//        List<Map<String, Object>> mainMapList = new ArrayList<>();
//        List<Map<String, Object>> subMapList = new ArrayList<>();
//        if (mainList != null && mainList.size() > 0) {
//            for (Object t : mainList) {
//                mainMapList.add(BeanUtils.toMap(t));
//            }
//            for (Object t : subList) {
//                subMapList.add(BeanUtils.toMap(t));
//            }
//            for (Map<String, Object> mainMap : mainMapList) {
//                Object mainId = mainMap.get(mainIdName);
//                if (subList != null && subList.size() > 0) {
//                    for (Map<String, Object> subMap : subMapList) {
//                        Object subId = subMap.get(subIdName);
//                        if (Objects.equals(mainId, subId)) {
//                            for (String key : subMap.keySet()) {
//                                if (!mainMap.containsKey(key)) {
//                                    mainMap.put(key, subMap.get(key));
//                                }
//                            }
//                            T instance = type.newInstance();
//                            BeanUtils.populate(instance).from(mainMap);
//                            resultList.add(instance);
//                            break;
//                        }
//                    }
//                } else {
//                    T instance = type.newInstance();
//                    BeanUtils.populate(instance).from(mainMap);
//                    resultList.add(instance);
//                }
//            }
//        }
//        return resultList;
//    }
//
//    public static Map<String, Object> convertBeanToMap(Object bean) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
//        Class type = bean.getClass();
//        Map<String, Object> returnMap = new HashMap<String, Object>();
//        BeanInfo beanInfo = Introspector.getBeanInfo(type);
//        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//        for (int i = 0; i < propertyDescriptors.length; i++) {
//            PropertyDescriptor descriptor = propertyDescriptors[i];
//            String propertyName = descriptor.getName();
//            if (!propertyName.equals("class")) {
//                Method readMethod = descriptor.getReadMethod();
//                Object result = readMethod.invoke(bean, new Object[0]);
//                if (result != null) {
//                    returnMap.put(propertyName, result);
//                } else {
//                    returnMap.put(propertyName, "");
//                }
//            }
//        }
//        return returnMap;
//    }
//
//    /**
//     * 将一个 Map 对象转化为一个 JavaBean
//     *
//     * @param type 要转化的类型
//     * @param map  包含属性值的 map
//     * @return 转化出来的 JavaBean 对象
//     * @throws IntrospectionException    如果分析类属性失败
//     * @throws IllegalAccessException    如果实例化 JavaBean 失败
//     * @throws InstantiationException    如果实例化 JavaBean 失败
//     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
//     */
//    public static Object convertMap(Class type, Map map)
//            throws IntrospectionException, IllegalAccessException,
//            InstantiationException, InvocationTargetException {
//        BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
//        Object obj = type.newInstance(); // 创建 JavaBean 对象
//
//        // 给 JavaBean 对象的属性赋值
//        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
//        for (int i = 0; i < propertyDescriptors.length; i++) {
//            PropertyDescriptor descriptor = propertyDescriptors[i];
//            String propertyName = descriptor.getName();
//
//            if (map.containsKey(propertyName)) {
//                // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
//                Object value = map.get(propertyName);
//
//                Object[] args = new Object[1];
//                args[0] = value;
//
//                descriptor.getWriteMethod().invoke(obj, args);
//            }
//        }
//        return obj;
//    }
}

