package ru.otus.homework.core.units;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.exceptions.ATMMachineCellExceedingLimitException;
import ru.otus.homework.exceptions.ATMMachineCellNotEnoughAvaibaleException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleATMMachineCellTest {

    @Test
    void checkCellCreation() {

        ATMMachineCell cell = new SimpleATMMachineCell(NominalValue._50, 14, 9);

        assertThat(cell.getNominal())
                .isEqualTo(NominalValue._50);

        assertThat(cell.avaibale())
                .isEqualTo(5);

        assertThat(cell.getLimit())
                .isEqualTo(14);

    }

    @Test
    void checkPutOperation() {

        ATMMachineCell cell = new SimpleATMMachineCell(NominalValue._50, 14, 9);

        cell.put(1);
        cell.put(2);

        assertThat(cell.avaibale())
                .isEqualTo(2);

        assertThrows(ATMMachineCellExceedingLimitException.class,
                () -> {
                    cell.put(12);
                });

    }

    @Test
    void checkGetOperation() {

        ATMMachineCell cell = new SimpleATMMachineCell(NominalValue._50, 14, 9);

        cell.get(5);
        cell.get(3);

        assertThat(cell.avaibale())
                .isEqualTo(13);

        assertThrows(ATMMachineCellNotEnoughAvaibaleException.class,
                () -> {
                    cell.get(1000);
                });

    }

}