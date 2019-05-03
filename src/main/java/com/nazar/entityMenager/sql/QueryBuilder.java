package com.nazar.entityMenager.sql;

import com.nazar.entityMenager.TableDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {
    private String query;
    private VeryBadClass typeMapper;
    private int setCounter;

    public QueryBuilder() {
        this.query = "";
        this.typeMapper = new VeryBadClass();
        setCounter = 0;
    }

    public QueryBuilder dropTable() {
        this.query = this.query + "DROP TABLE IF EXISTS";
        return this;
    }

    public QueryBuilder setTableName(String tableName) {
        this.query = this.query + " " + tableName + " ";
        return this;
    }

    public QueryBuilder selectAllFrom() {
        this.query = this.query + "SELECT * FROM";
        return this;
    }

    public QueryBuilder where(String argument, Object value) {
        this.query = this.query + "where " + argument + " = " + value;
        return this;
    }

    public QueryBuilder deleteFrom() {
        this.query = this.query + "DELETE FROM ";
        return this;
    }

    public QueryBuilder update() {
        this.query = this.query + "UPDATE ";
        return this;
    }

    public QueryBuilder set(String argument, Object value) {
        if (setCounter == 0) {
            this.query = this.query + " SET ";
        }
        if (setCounter > 0) {
            this.query = this.query + ",";
        }
        this.setCounter++;
        this.query = this.query + argument + " = " + "'" + value + "'";
        return this;
    }

    public QueryBuilder insertInto(String table, List<String> arguments, List<Object> values) {
        List<String> stringList = values.stream().map(Object::toString).collect(Collectors.toList());
        this.query = "INSERT INTO " + table + "(" + String.join(",", arguments) + ")" + " values " + "(" + String.join(",", stringList) + ")";
        return this;
    }

    public QueryBuilder insertInto(String table, List<String> arguments, String values) {
        this.query = "INSERT INTO " + table + "(" + String.join(",", arguments) + ")" + " values " + "(" + values + ")";
        return this;
    }

    public QueryBuilder createTable(boolean ifNotExists) {
        this.query = this.query + "CREATE TABLE ";
        if (ifNotExists) {
            this.query = this.query + "IF NOT EXISTS ";
        }
        return this;
    }

    public QueryBuilder addColumns(TableDescription tableDescription) {
        String row;
        ArrayList<String> columnList = new ArrayList<>();
        for (String columnName : tableDescription.getColumnNames()) {
            row = columnName + " " + this.typeMapper.defineSqlType(tableDescription, columnName) + addAutoIncrementIfColumnIsPrimaryKey(columnName, tableDescription);
            columnList.add(row);
        }
        String columns = String.join(",", columnList);
        this.query = this.query + "(" + columns + " ,";
        this.query = this.query + "PRIMARY KEY (" + tableDescription.getPrimaryKey() + "));";
        return this;
    }

    private String addAutoIncrementIfColumnIsPrimaryKey(String columnName, TableDescription tableDescription) {
        String primaryKeyColumnName = tableDescription.getPrimaryKey();
        if (columnName.equals(primaryKeyColumnName)) {
            return " NOT NULL AUTO_INCREMENT";
        }
        return "";
    }

    public String build() {
        return this.query;
    }

}
