package com.nazar.entityMenager;

import com.nazar.annotations.Column;
import com.nazar.annotations.Entity;
import com.nazar.annotations.Id;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TableDescription {
    private Object entity;
    private Class clazz;
    private String tableName;
    private String primaryKey;
    private Map<String, ColumnDescription> colums;

    public TableDescription(Object object) {
        this.entity = object;
        this.clazz = object.getClass();
        init();
    }

    public TableDescription(Class clazz) {
        colums = new LinkedHashMap<>();
        this.clazz = clazz;
        findTableName();
        findColumns();
        findPrimaryKey();
    }

    private void init() {
        colums = new LinkedHashMap<>();
        findTableName();
        findColumns();
        findPrimaryKey();
    }

    public static boolean isNotEntity(Class clazz) {
        boolean isEntity = true;
        if (clazz.isAnnotationPresent(Entity.class)) {
            isEntity = false;
        }
        return isEntity;
    }

    public static boolean isNotEntity(Object object) {
        boolean isEntity = true;
        if (object.getClass().isAnnotationPresent(Entity.class)) {
            isEntity = false;
        }
        return isEntity;
    }


    public Map<String, ColumnDescription> getColums() {
        return colums;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnNames() {
        String[] columnNames = colums.keySet().stream().toArray(String[]::new);
        return Arrays.asList(columnNames);
    }

    private void findTableName() {
        String tableName;
        Class clazz = this.clazz;
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (!entity.value().isEmpty()) {
            tableName = entity.value();
        } else {
            tableName = clazz.getSimpleName();
        }
        this.tableName = tableName;
    }

    private void findColumns() {
        Field[] fields = this.clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                ColumnDescription columnDescription = new ColumnDescription();
                columnDescription.setColumnType(field.getType().getSimpleName());
                this.colums.put(field.getName(), columnDescription);
            }
        }
    }

    private void findPrimaryKey() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                this.primaryKey = field.getName();
            }
        }
    }

    public static Object getPrimaryKeyValueFrom(Object object) {
        TableDescription tableDescription = new TableDescription(object);
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        Object primaryKeyValue = null;
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().equals(tableDescription.getPrimaryKey())) {
                try {
                    primaryKeyValue = field.get(object);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return primaryKeyValue;

    }

    public String getQuestionMarks() {
        return getQuestionMarksAccordingToQuantityOfColumn();
    }

    private String getQuestionMarksAccordingToQuantityOfColumn() {
        String marks = "";
        if (!colums.isEmpty()) {
            for (int i = 0; i < colums.size(); i++) {
                if (!marks.equals("")) {
                    marks = marks.concat("," + "?");
                } else {
                    marks = marks.concat("?");
                }
            }
        }
        return marks;
    }

    public class ColumnDescription {
        private Object columnType;


        public Object getColumnType() {
            return columnType;
        }

        public String getColumnTypeName() {
            return columnType.toString();
        }

        public void setColumnType(Object columnType) {
            this.columnType = columnType;
        }
    }
}
