package ru.otus.homework.core;

import ru.otus.homework.core.manipulators.PutManipulator;
import ru.otus.homework.core.manipulators.ServiceManipulator;
import ru.otus.homework.core.manipulators.WithdrawalManipulator;
import ru.otus.homework.core.units.CurrencyUnit;
import ru.otus.homework.core.units.NominalValue;
import ru.otus.homework.core.units.SimpleATMMachineCell;
import ru.otus.homework.core.interfaces.ATMMachine;
import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.exceptions.ATMMachineInsufficientFundsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleATMMachine implements ATMMachine {

    private Map<NominalValue, List<ATMMachineCell>> cells;
    private WithdrawalManipulator withdrawalManipulator;
    private PutManipulator putManipulator;
    private ServiceManipulator serviceManipulator;

    private SimpleATMMachine(Map<NominalValue, List<ATMMachineCell>> cells,
                             WithdrawalManipulator withdrawalManipulator,
                             PutManipulator putManipulator,
                             ServiceManipulator serviceManipulator) {
        this.cells = cells;
        this.withdrawalManipulator = withdrawalManipulator;
        this.putManipulator = putManipulator;
        this.serviceManipulator = serviceManipulator;
    }

    @Override
    public void deposit(CurrencyUnit[] moneys) {
        putManipulator.deposit(moneys, cells);
    }

    @Override
    public List<CurrencyUnit> withdraw(long amount) {
        if (balance() < amount){
            throw new ATMMachineInsufficientFundsException();
        }
        return withdrawalManipulator.get(amount, cells);
    }

    @Override
    public long balance() {
        return serviceManipulator.getBalanceForCells(cells);
    }

    public static SimpleATMMachineBuilder builder(WithdrawalManipulator withdrawalManipulator,
                                                  PutManipulator putManipulator,
                                                  ServiceManipulator serviceManipulator){
        return new SimpleATMMachineBuilder(withdrawalManipulator, putManipulator, serviceManipulator);
    }

    static class SimpleATMMachineBuilder{

        private WithdrawalManipulator withdrawalManipulator;
        private PutManipulator putManipulator;
        private ServiceManipulator serviceManipulator;
        private Map<NominalValue, List<ATMMachineCell>> cells = new HashMap<NominalValue, List<ATMMachineCell>>();

        private SimpleATMMachineBuilder(WithdrawalManipulator withdrawalManipulator,
                                       PutManipulator putManipulator,
                                       ServiceManipulator serviceManipulator) {

            this.withdrawalManipulator = withdrawalManipulator;
            this.putManipulator = putManipulator;
            this.serviceManipulator = serviceManipulator;

            if (withdrawalManipulator == null){
                throw new IllegalArgumentException();
            }

            if (putManipulator == null){
                throw new IllegalArgumentException();
            }

            if (serviceManipulator == null){
                throw new IllegalArgumentException();
            }

        }

        public SimpleATMMachineBuilder addCell(ATMMachineCell cell) {

            List<ATMMachineCell> atmCells = null;
            if (cells.containsKey(cell.getNominal())){
                atmCells = cells.get(cell.getNominal());
            } else {
                atmCells = new ArrayList<>();
            }

            atmCells.add(cell);
            cells.put(cell.getNominal(), atmCells);

            return this;

        }

        public SimpleATMMachine build(){
            return new SimpleATMMachine(cells, withdrawalManipulator, putManipulator, serviceManipulator);
        }

    }

}
