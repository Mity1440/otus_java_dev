package ru.otus.homework.core;

import ru.otus.homework.annotations.Log;

public class CalculatableImpl implements Calculatable{

    @Log
    @Override
    public void calculate() {
        System.out.println("calculate()");
    }

    @Log
    @Override
    public void calculate(int a, int b) {
        System.out.println("calculate(int a, int b):"
                           +" a + b = "
                           +(a + b));
    }

    @Override
    public void calculate(int a, int b, int c) {
        System.out.println("calculate(int a, int b, int c):"
                            +" a + b + c = "
                            +(a + b + c ));
    }

}
