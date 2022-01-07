package ru.otus.homework.core.units;

public enum NominalValue {

    _50(50),
    _100(100),
    _200(200);

    private final int cost;

    NominalValue(int cost) {
        this.cost = cost;
    }

    public int getCost(){
        return cost;
    }

}
