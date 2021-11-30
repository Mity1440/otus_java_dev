package ru.otus.homework1;

import ru.otus.homework1.core.CustomClassloader;
import ru.otus.homework1.model.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {

        CustomClassloader cl = new CustomClassloader();

        Class<?> clazz = cl.findClass("ru.otus.homework1.model.Test");
        Object instance = clazz.getDeclaredConstructor().newInstance();

        Method method = clazz.getDeclaredMethod("method1");
        method.invoke(instance);

        Method method1 = clazz.getDeclaredMethod("method1", String.class, int.class);
        method1.invoke(instance, "raharoh", 9);

        Method method2 = clazz.getDeclaredMethod("method1", String.class, int.class, int.class);
        method2.invoke(instance, "raharoh-raharoh", 9, 7);

        Method method3 = clazz.getDeclaredMethod("anotherMethod1");
        method3.invoke(instance);

    }

}
