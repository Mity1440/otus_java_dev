package core;

import annotations.After;
import annotations.Before;
import annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestClassContext {

    private Class<?> clazz;
    private Map<MethodCallOrder, ArrayList<Method>> testMethods;

    public TestClassContext() {
        testMethods = new HashMap<>();
    }

    public void initialize(Class<?> clazz) {

        this.clazz = clazz;
        testMethods.clear();

        Method[] allMethods = clazz.getDeclaredMethods();
        for (Method method: allMethods){

            checkAndAddMethodIntoTestMethods(After.class, method);
            checkAndAddMethodIntoTestMethods(Before.class, method);
            checkAndAddMethodIntoTestMethods(Test.class, method);

        }

    }

    public Object getDefaultObjectInstance() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    public ArrayList<Method> getTestMethods(MethodCallOrder type){
        return testMethods.get(type);
    }

    private void addTestMethod(MethodCallOrder type, Method method) {

        ArrayList<Method> typeValues = null;
        if (testMethods.containsKey(type)) {
            typeValues = testMethods.get(type);
        } else {
            typeValues = new ArrayList<Method>();
        }

        typeValues.add(method);

        testMethods.put(type, typeValues);

    }

    private void checkAndAddMethodIntoTestMethods(Class<? extends Annotation> annotation, Method method) {

        if (method.isAnnotationPresent(annotation)){
            addTestMethod(getTypeForAnnotations(annotation), method);
        }

    }

    private MethodCallOrder getTypeForAnnotations(Class<? extends Annotation> annotation) {

        if (annotation.equals(After.class)){
            return MethodCallOrder.AFTER;
        }else if (annotation.equals(Before.class)){
            return MethodCallOrder.BEFORE;
        }else if (annotation.equals(Test.class)){
            return MethodCallOrder.TEST;
        }

        throw new RuntimeException();

    }


    public String getTestingClassName() {
        return clazz.getName();
    }

    public Class<?> getTestClass() {
        return clazz;
    }
}
