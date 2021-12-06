package ru.otus.homework.core.interfaces;

import ru.otus.homework.core.units.NominalValue;

public interface ATMMachineCell {

    void put(int count);

    void get(int count);

    NominalValue getNominal();

    int getLimit();

    int getCount();

    int avaibale();

}
