package ru.otus.homework.core.manipulators;

import ru.otus.homework.core.interfaces.ATMMachine;
import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.core.units.CurrencyUnit;
import ru.otus.homework.core.units.NominalValue;
import ru.otus.homework.exceptions.ATMWithdrawalManipulatorDebitingMoneyException;

import java.util.*;
import java.util.stream.Collectors;

public class WithdrawalManipulator {

    public List<CurrencyUnit> get(long amount, Map<NominalValue, List<ATMMachineCell>> cells) {

        List<CurrencyUnit> cash = new ArrayList<>();

        var sortesCells = getSortedCells(cells);
        var transactions = getDebitTransactions(amount, sortesCells);

        for (var transaction: transactions){
            var currencyUnits = transaction.execute();
            for(var currencyUnit: currencyUnits){
                cash.add(currencyUnit);
            }
        }

        return cash;

    }

    private Map<NominalValue, List<ATMMachineCell>> getSortedCells(Map<NominalValue, List<ATMMachineCell>> cells){

        Map<NominalValue, List<ATMMachineCell>> sortedCells =
                new TreeMap<>(Comparator.comparingInt(NominalValue::getCost).reversed());

        sortedCells.putAll(cells);
        sortedCells.values().forEach(o->{
            o.sort(Comparator.comparingLong(ATMMachineCell::avaibale).reversed());
        });

        return sortedCells;

    }

    private List<DebitTransaction> getDebitTransactions(long amount, Map<NominalValue, List<ATMMachineCell>> cells){

        var debit = amount;
        var transactions = new ArrayList<DebitTransaction>();

        for (var cell: cells.entrySet()){

            var cost = cell.getKey().getCost();
            if (cost > debit){
                continue;
            }

            var debitCells = cell.getValue();
            var debitCount = (int) debit / cost;

            for (var debitCell: debitCells){

                if (debitCell.getCount() == 0 || debitCount == 0){
                    break;
                }

                var debitFromCell = Math.min(debitCell.getCount(), debitCount);
                transactions.add(new DebitTransaction(debitCell, debitFromCell));

                debitCount -= debitFromCell;
                debit -= debitFromCell * cost;

            }

        }

        if (debit != 0){
            throw new ATMWithdrawalManipulatorDebitingMoneyException();
        }

        return transactions;
    }

    private class DebitTransaction {

        private final ATMMachineCell cell;
        private final int debit;

        public DebitTransaction(ATMMachineCell cell, int debit) {
            this.cell = cell;
            this.debit = debit;
        }

        public CurrencyUnit[] execute() {

            CurrencyUnit[] unit = new CurrencyUnit[debit];
            for (int i = 0; i < debit; i++){
                unit[i] = new CurrencyUnit(cell.getNominal());
            }
            cell.get(debit);

            return unit;

        }
    }

}
