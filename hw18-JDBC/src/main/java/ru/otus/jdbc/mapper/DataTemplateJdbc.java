package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> classMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> classMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.classMetaData = classMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {

        return dbExecutor.executeSelect(connection,
                                        entitySQLMetaData.getSelectByIdSql(),
                                        List.of(id),
                                        this::getInstanceFromResultSet);

    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection,
                                        entitySQLMetaData.getSelectAllSql(),
                                        Collections.emptyList(),
                                        this::getAllInstanceFromResultSet)
                         .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T client) {
        return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), getFieldValuesForInsert(client));
    }

    @Override
    public void update(Connection connection, T client) {
        dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), getFieldValuesForUpdate(client));
    }

    // internal

    private T getInstanceFromResultSet(ResultSet resultSet) {

        try {
            if (resultSet.next()) {
                return createInstance(resultSet);
            }
        } catch (SQLException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error during creating instance");
        }

        return null;

    }

    private T createInstance(ResultSet resultSet) throws InvocationTargetException, InstantiationException, IllegalAccessException {

        var newInstance = classMetaData.getConstructor().newInstance();

        classMetaData.getAllFields().forEach(field->{

            try {
                var fieldValue = resultSet.getObject(field.getName());
                field.set(newInstance, fieldValue);
            } catch (SQLException | IllegalAccessException e) {
                throw new RuntimeException("Error during get field value");
            }

        });

        return newInstance;

    }

    private List<T> getAllInstanceFromResultSet(ResultSet resultSet) {

        List<T> allInstance = new ArrayList<>();
        try {
            while (resultSet.next()) {
                allInstance.add(createInstance(resultSet));
            }
        } catch (SQLException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("Error during creating instances");
        }
        return  allInstance;
    }

    private List<Object> getFieldValuesForInsert(T object) {
        List<Object> fieldValues = new ArrayList<>();
        for (Field field : classMetaData.getFieldsWithoutId()) {
            try {
                field.setAccessible(true);
                fieldValues.add(field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error during creating instance");
            }
        }
        return fieldValues;
    }

    private List<Object> getFieldValuesForUpdate(T client) {
        List<Object> fieldValues = getFieldValuesForInsert(client);
        try {
            fieldValues.add(classMetaData.getIdField().get(client));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error during creating instance");
        }
        return fieldValues;
    }

}
