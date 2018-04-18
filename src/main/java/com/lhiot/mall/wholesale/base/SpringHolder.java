package com.lhiot.mall.wholesale.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

public class SpringHolder implements ApplicationContextAware, EnvironmentAware {

    private static ApplicationContext applicationContext;

    private static Environment environment;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringHolder.applicationContext = applicationContext;
    }

    @Override
    public void setEnvironment(Environment environment) {
        SpringHolder.environment = environment;
    }

    public static void publishEvent(Object event) {
        applicationContext.publishEvent(event);
    }

    public static void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return applicationContext.getBean(requiredType);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static String getProperty(String name) {
        return environment.getProperty(name);
    }

    public static <T> T getProperty(String name, Class<T> type) {
        return environment.getProperty(name, type);
    }

    public static <T> T getProperty(String name, Class<T> type, T defaultValue) {
        return environment.getProperty(name, type, defaultValue);
    }
}
