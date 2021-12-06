package ru.otus.homework.core.manipulators;

import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.core.units.CurrencyUnit;
import ru.otus.homework.core.units.NominalValue;
import ru.otus.homework.exceptions.ATMMachineCellNotEnoughAvaibaleException;
import ru.otus.homework.exceptions.ATMMachineNoExistingNominalValueException;
import ru.otus.homework.exceptions.ATMPutManagerDistributionException;
import ru.otus.homework.exceptions.ATMPutManagerNotRecognizeUnit;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PutManipulator {

    public void deposit(CurrencyUnit[] moneys, Map<NominalValue, List<ATMMachineCell>> cells ){
        CheckThePossibilityOfDepositingCash(moneys, cells);
        distributeMoneyOnCells(moneys, cells);
    }

    private void CheckThePossibilityOfDepositingCash(CurrencyUnit[] moneys,
                                                     Map<NominalValue, List<ATMMachineCell>> cells) {

        checkRecognitionOfBanknotes(moneys);
        Map<NominalValue, Integer> moneysOnNominalValue = moneysOnNominalValue(moneys);
        moneysOnNominalValue.entrySet().stream().forEach(o->{

            if (!cells.containsKey(o.getKey())){
                throw new ATMMachineNoExistingNominalValueException();
            }

            long totalAvaibale = cells.get(o.getKey()).stream().mapToLong(ATMMachineCell::avaibale).sum();
            if (totalAvaibale < o.getValue()){
                throw new ATMMachineCellNotEnoughAvaibaleException();
            }

        });

    }

    private Map<NominalValue, Integer> moneysOnNominalValue(CurrencyUnit[] moneys) {
        return  Arrays.stream(moneys)
                .map(CurrencyUnit::getValue)
                .collect(Collectors.groupingBy(Function.identity(),
                        Collectors.summingInt(e -> 1)));
    }

    private void checkRecognitionOfBanknotes(CurrencyUnit[] moneys) {

        var notNullCount = Arrays.stream(moneys).filter(Objects::nonNull).count();
        if (notNullCount != moneys.length){
            throw new ATMPutManagerNotRecognizeUnit();
        }

    }

    private void distributeMoneyOnCells(CurrencyUnit[] moneys, Map<NominalValue, List<ATMMachineCell>> cells) {

        Map<NominalValue, Integer> moneysOnNominalValue = moneysOnNominalValue(moneys);

        moneysOnNominalValue.entrySet().stream().forEach(o->{

            int ToDistribution = o.getValue();
            List<ATMMachineCell> nominalCells = cells.get(o.getKey());

            for (ATMMachineCell nominalCell: nominalCells){

                if (ToDistribution < 0){
                    break;
                }

                int ToDistributionOnIterate = Math.min(ToDistribution, nominalCell.avaibale());
                nominalCell.put(ToDistributionOnIterate);

                ToDistribution -= ToDistributionOnIterate;

            }

            if (ToDistribution > 0){
                throw new ATMPutManagerDistributionException();
            }

        });

    }

}
