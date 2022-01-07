package ru.otus.homework.core.units;

public class CurrencyUnit {

    NominalValue value;

    public CurrencyUnit(NominalValue value) {
        this.value = value;
    }

    public NominalValue getValue() {
        return value;
    }

}
