package ru.otus.homework;

import ru.otus.homework.annotations.Log;
import ru.otus.homework.core.Calculatable;
import ru.otus.homework.core.CalculatableImpl;
import ru.otus.homework.core.CustomLogger;
import ru.otus.homework.core.Logger;
import ru.otus.homework.proxy.Ioc;

public class Main {

    public static void main(String[] args) {

        Logger customLogger = new CustomLogger();
        var calculatable = new CalculatableImpl();

        calculatable.calculate();
        calculatable.calculate(1,3);
        calculatable.calculate(1,2,3);

        var calculatableProxy = (Calculatable)Ioc.getDecaratedClass(calculatable, Log.class, customLogger);
        calculatableProxy.calculate();
        calculatableProxy.calculate(1,3);
        calculatableProxy.calculate(1,2,3);

    }

}
