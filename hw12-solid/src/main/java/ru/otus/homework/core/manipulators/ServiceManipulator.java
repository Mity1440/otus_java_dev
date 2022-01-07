package ru.otus.homework.core.manipulators;

import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.core.units.NominalValue;

import java.util.List;
import java.util.Map;

public class ServiceManipulator {

    public long getBalanceForCells(Map<NominalValue, List<ATMMachineCell>> cells) {

        var balanse = 0l;
        for (var cell: cells.entrySet()){
            var cost = cell.getKey().getCost();
            for (var atmCell: cell.getValue()){
                balanse +=  cost * atmCell.getCount();
            }
        }
        return balanse;

    }

}
