package ru.otus.homework.proxy;

import ru.otus.homework.core.CustomLogger;

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

    public static Object getDecaratedClass(Object o, Class<? extends Annotation> annotation) {

        InvocationHandler handler = new CustomInvocationHandler(o, annotation);

        Class clazz = o.getClass();
        Class[] clazzInterfaces = clazz.getInterfaces();

        return Proxy.newProxyInstance(o.getClass().getClassLoader(),
                                      clazzInterfaces,
                                      handler);
    }

    static class CustomInvocationHandler implements InvocationHandler {

        private final Object clazz;
        private final Set<String> annotatedMethods;
        private final CustomLogger logger;


        CustomInvocationHandler(Object myClass, Class<? extends Annotation> annotation) {

            this.clazz = myClass;

            logger = new CustomLogger();

            annotatedMethods =
            Arrays
                .stream(myClass.getClass().getDeclaredMethods())
                    .map(o->{
                        if (o.isAnnotationPresent(annotation)){
                            return getSignature(o);
                        }
                        return null;})
                           .filter(Objects::nonNull)
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

            StringBuilder sb = new StringBuilder();
            sb.append(method.getName());
            sb.append("_");

            Arrays
               .stream(method.getParameters())
                    .forEach(o->{
                        sb.append(o.getParameterizedType());
                        sb.append("_");
                    });

            return sb.toString();

        }

    }

}
