package com.nazar.entityMenager.sql;

import com.nazar.annotations.Column;
import com.nazar.entityMenager.TableDescription;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VeryBadClass {

    static String defineSqlType(TableDescription tableDescription, String columnName) {
        String sqlType = "";
        TableDescription.ColumnDescription columnDescription = tableDescription.getColums().get(columnName);
        String type = columnDescription.getColumnTypeName();
        switch (type) {
            case "Integer":
                sqlType = "INT";
                break;
            case "int":
                sqlType = "INT";
                break;
            case "String":
                sqlType = "Varchar(20)";
                break;

        }
        return sqlType;
    }

    public static void setValues(Object object, PreparedStatement statement) {
        int counter = 0;
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                try {
                    field.setAccessible(true);
                    Class columnValue = field.get(object).getClass();
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


    public static  <T> T getFromResultSet(ResultSet resultSet, TableDescription tableDescription, Class<T> clazz) throws SQLException, IllegalAccessException {
        T object = null;
        try {
            object = clazz.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        ArrayList<TableDescription.ColumnDescription> list = new ArrayList<>(tableDescription.getColums().values());
        String[] columnNames = tableDescription.getColumnNames().stream().toArray(String[]::new);
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < list.size(); i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String columnName = columnNames[i];
            String columnType = list.get(i).getColumnTypeName();
            Object result = null;
            switch (columnType) {
                case "String":
                    result = resultSet.getString(columnName);
                    break;
                case "int":
                    result = resultSet.getInt(columnName);
                    break;
                case "Integer":
                    result = resultSet.getInt(columnName);
                    break;
            }
            field.set(object, result);
        }
        return object;
    }
}
