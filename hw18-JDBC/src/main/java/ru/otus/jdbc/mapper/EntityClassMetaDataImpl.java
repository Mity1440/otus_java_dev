package ru.otus.jdbc.mapper;

import ru.otus.crm.model.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T>{

    private final String name;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> allFieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {

        this.name = clazz.getSimpleName();
        this.constructor = getConstructorFromClass(clazz);
        this.allFields = getAllFieldsFromClass(clazz);
        this.idField = getIdFieldFromAllFields();
        this.allFieldsWithoutId = getAllFieldsWithoutId();

    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return this.constructor;
    }

    @Override
    public Field getIdField() {
        return this.idField;
    }

    @Override
    public List<Field> getAllFields() {
        return this.allFields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return this.allFieldsWithoutId;
    }

    // Internal

    private List<Field> getAllFieldsWithoutId() {
        return
                allFields
                        .stream()
                        .filter(o -> !o.isAnnotationPresent(Id.class))
                        .collect(Collectors.toList());
    }

    private Field getIdFieldFromAllFields() {

        return
                allFields
                        .stream()
                        .filter(o -> o.isAnnotationPresent(Id.class))
                        .findFirst()
                        .orElseThrow(()->new RuntimeException("Not found \"ID\" field "));

    }

    private List<Field> getAllFieldsFromClass(Class<T> clazz) {

        List<Field> result = new ArrayList<>();
        for(var field: clazz.getDeclaredFields()){
            field.setAccessible(true);
            result.add(field);
        }

        return result;
    }

    private Constructor<T> getConstructorFromClass(Class<T> clazz) {

        Constructor<T> constructor = null;
        try {
            constructor = clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Not found default constructor");
        }

        return constructor;

    }


}
