package ru.otus.homework1.model;

import ru.otus.homework1.annotation.Log;

public class Test {

    @Log
    public void method1(){
        System.out.println("I am from method1");
    }

    @Log
    public void method1(String s, int b){
        System.out.println("I am from method1, my params: " + s + ", " + b);
    }

    public void method1(String s, int a, int b){
        System.out.println("I am from method1, my params: "
                           + s
                           + ", "
                           + a
                           + ", "
                           + b);
    }

    @Log
    public static void anotherMethod1(){
        System.out.println("I am from anotherMethod1, my params: ");
    }

}
