package ru.otus.homework.core.interfaces;

import ru.otus.homework.core.units.CurrencyUnit;

import java.util.List;

public interface ATMMachine {

    void deposit(CurrencyUnit[] moneys);

    List<CurrencyUnit> withdraw(long amount);

    long balance();

}
