package ru.otus.homework.core;

import java.lang.reflect.Method;

public interface Logger {

    void log(Method method, Object[] args);

}
