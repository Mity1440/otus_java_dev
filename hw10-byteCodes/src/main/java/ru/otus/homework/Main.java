package ru.otus.homework;

import ru.otus.homework.annotations.Log;
import ru.otus.homework.core.Calculatable;
import ru.otus.homework.core.CalculatableImpl;
import ru.otus.homework.proxy.Ioc;

public class Main {

    public static void main(String[] args) {

        var CalculatableImpl = new CalculatableImpl();

        CalculatableImpl.calculate();
        CalculatableImpl.calculate(1,3);
        CalculatableImpl.calculate(1,2,3);

        var CalculatableImplProxy = (Calculatable)Ioc.getDecaratedClass(CalculatableImpl, Log.class);
        CalculatableImplProxy.calculate();
        CalculatableImplProxy.calculate(1,3);
        CalculatableImplProxy.calculate(1,2,3);

    }

}
