package ru.otus.homework.proxy;

import ru.otus.homework.core.CustomLogger;
import ru.otus.homework.core.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Ioc {

    private Ioc() {
    }

    public static Object getDecaratedClass(Object o, Class<? extends Annotation> annotation, Logger logger) {

        InvocationHandler handler = new CustomInvocationHandler(o, annotation, logger);

        Class<?> clazz = o.getClass();
        Class<?>[] clazzInterfaces = clazz.getInterfaces();

        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      clazzInterfaces,
                                      handler);
    }

    static class CustomInvocationHandler implements InvocationHandler {

        private final Object clazz;
        private final Set<String> annotatedMethods;
        private final Logger logger;


        CustomInvocationHandler(Object myClass,
                                Class<? extends Annotation> annotation,
                                Logger logger) {

            this.clazz = myClass;
            this.logger = logger;

            annotatedMethods = Arrays
                    .stream(myClass.getClass().getDeclaredMethods())
                    .filter(o -> o.isAnnotationPresent(annotation))
                    .map(this::getSignature)
                    .collect(Collectors.toSet());

        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if (annotatedMethods.contains(getSignature(method))){
                logger.log(method, args);
            }

            return method.invoke(clazz, args);

        }

        @Override
        public String toString() {
            return "CustomInvocationHandler{" +
                    "myClass=" + clazz +
                    '}';
        }

        private String getSignature(Method method){
            return method.getName() + Arrays.toString(method.getParameters());
        }

    }

}
