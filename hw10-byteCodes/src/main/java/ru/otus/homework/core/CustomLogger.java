package ru.otus.homework.core;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CustomLogger {

    private final PrintStream out;

    public CustomLogger() {
        this.out = System.out;
    }

    public void log(Method method, Object[] args){
        this.out.println("executed method: "
                           + method.getName()
                           + " params: "
                           + Arrays.toString(args));
    }

}
