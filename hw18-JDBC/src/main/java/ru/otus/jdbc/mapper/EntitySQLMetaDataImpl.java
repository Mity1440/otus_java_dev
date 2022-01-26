package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData{

    private final EntityClassMetaData<T> entityClassMetaData;

    private final static String SELECT_TEMPLATE = "SELECT %fields% from %tableName%";
    private final static String SELECT_TEMPLATE_BY_ID= "SELECT %fields% from %tableName% where %idFieldName% = ?";
    private final static String INSERT_TEMPLATE = "INSERT into %tableName%(%fields%) values (%fieldsMask%)";
    private final static String UPDATE_TEMPLATE = "UPDATE %tableName% SET %fieldsMask% WHERE %idFieldName% = ?";

    public EntitySQLMetaDataImpl(EntityClassMetaData entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {

        return SELECT_TEMPLATE
                .replace("%fields%", getAllEntityFieldsInString())
                .replace("%tableName%", getEntityName());
    }
    @Override
    public String getSelectByIdSql() {

        return SELECT_TEMPLATE_BY_ID
                .replace("%fields%", getAllEntityFieldsInString())
                .replace("%tableName%", getEntityName())
                .replace("%idFieldName%", entityClassMetaData.getIdField().getName());

    }

    @Override
    public String getInsertSql() {

        return INSERT_TEMPLATE
                .replace("%tableName%", getEntityName())
                .replace("%fields%", getAllEntityFieldsWithoutIdInString())
                .replace("%fieldsMask%", getInsertFieldsMask());

    }

    @Override
    public String getUpdateSql() {

        return UPDATE_TEMPLATE
                .replace("%tableName%", getEntityName())
                .replace("%idFieldName%", entityClassMetaData.getIdField().getName())
                .replace("%fieldsMask%", getUpdateFieldsMask());

    }

    // Internal

    private String getEntityName() {
        return entityClassMetaData.getName();
    }

    private String getAllEntityFieldsInString() {
        return entityClassMetaData
                .getAllFields()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
    }

    private String getAllEntityFieldsWithoutIdInString() {

        return entityClassMetaData
                .getFieldsWithoutId()
                .stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));

    }

    private String getInsertFieldsMask() {
        return entityClassMetaData
                .getFieldsWithoutId()
                .stream()
                .map(o->"?")
                .collect(Collectors.joining(", "));
    }

    private String getUpdateFieldsMask() {

        return entityClassMetaData
                .getFieldsWithoutId()
                .stream()
                .map(o->{
                    return o.getName() + " = ?";
                })
                .collect(Collectors.joining(", "));

    }

}
