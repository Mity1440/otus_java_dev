package ru.otus;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class HelloOtus{
    public static void main(String[]args){

        System.out.println("Hello OTUS");

        BiMap daysOfWeek = HashBiMap.create();

        daysOfWeek.put(1, "Monday");
        daysOfWeek.put(2, "Tuesday");
        daysOfWeek.put(3, "Wednesday");
        daysOfWeek.put(4, "Thursday");
        daysOfWeek.put(5, "Friday");
        daysOfWeek.put(6, "Saturday");
        daysOfWeek.put(7, "Sunday");

        System.out.println(daysOfWeek);

    }
}
