package com.nazar.entityMenager;

import com.nazar.annotations.Column;
import com.nazar.annotations.Entity;
import com.nazar.annotations.Id;

import java.lang.reflect.Field;
import java.util.*;

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
        this.clazz = clazz;
        findTableName();
        findColumns();
    }

    private void init() {
        colums = new LinkedHashMap<>();
        findTableName();
        findColumns();
        findPrimaryKey();
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

    public Set<String> getColumnNames() {
        return colums.keySet();
    }

    private void findTableName() {
        String tableName;
        Class clazz = this.clazz;
        Entity entity = (Entity) clazz.getAnnotation(Entity.class);
        if (!entity.value().equals("")) {
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
                System.out.println(field.getType().getSimpleName() + " < -------------------------------------");
                columnDescription.setColumnType(field.getType().getSimpleName());
                this.colums.put(field.getName(), columnDescription);
            }
        }
    }

    private void findPrimaryKey() {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                this.primaryKey = field.getName();
            }
        }
    }

   /* private void findColumnValues() {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                try {

                    Class columnValue = field.get(entity).getClass();
                    switch (columnValue.getSimpleName()) {
                        case "Integer":
                            statement.setInt(++counter, (Integer) field.get(object));
                            break;
                        case "String":
                            statement.setString(++counter, (String) field.get(object));
                            break;
                    }
                } catch (IllegalAccessException | SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/


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
