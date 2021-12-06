package ru.otus.homework.core.units;

import ru.otus.homework.exceptions.ATMMachineCellExceedingLimitException;
import ru.otus.homework.core.interfaces.ATMMachineCell;
import ru.otus.homework.exceptions.ATMMachineCellNotEnoughAvaibaleException;

public class SimpleATMMachineCell implements ATMMachineCell {

    private final NominalValue nominal;
    private final int limit;
    private int count;

    public SimpleATMMachineCell(NominalValue nominal, int limit) {
        this(nominal, limit, 0);
    }

    public SimpleATMMachineCell(NominalValue nominal, int limit, int count) {
        this.nominal = nominal;
        this.limit = limit;
        this.count = count;
    }

    @Override
    public void put(int count) {
        if (limitIsExceededAfterPut(count)){
            throw new ATMMachineCellExceedingLimitException();
        }
        this.count += count;
    }

    @Override
    public void get(int count) {
        if (this.count - count < 0){
            throw new ATMMachineCellNotEnoughAvaibaleException();
        }
        this.count -= count;
    }

    @Override
    public NominalValue getNominal() {
        return nominal;
    }

    @Override
    public int getLimit() {
        return limit;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int avaibale(){
        return this.limit - this.count;
    }

    private boolean limitIsExceededAfterPut(long count){
        return (this.limit - this.count - count) < 0;
    }

}
