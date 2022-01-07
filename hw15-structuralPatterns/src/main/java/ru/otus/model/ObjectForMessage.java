package ru.otus.model;

import java.util.ArrayList;
import java.util.List;

public class ObjectForMessage implements Cloneable{
    private List<String> data;

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    @Override
    public ObjectForMessage clone() throws CloneNotSupportedException {

        var demoCopy = new ArrayList<>(data);

        var result =  new ObjectForMessage();
        result.setData(demoCopy);

        return result;

    }
}
