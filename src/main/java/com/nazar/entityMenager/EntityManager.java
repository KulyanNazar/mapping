package com.nazar.entityMenager;

import com.nazar.entityMenager.sql.QueryBuilder;
import com.nazar.entityMenager.sql.VeryBadClass;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EntityManager implements DbOperations {
    private EntityManagerConfiguration configuration;
    private Connection connection;

    private Connection openConnection() {
        try {
            this.connection = DriverManager.getConnection(configuration.getURL(), configuration.getUser(), configuration.getPassword());
        } catch (SQLException e) {
            System.out.println("!!!!!!!!!!!! ERROR");
        }
        return this.connection;
    }

    private void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("!!!!!!!!!!!! ERROR");
        }
    }

    public EntityManager(EntityManagerConfiguration configuration) {
        this.configuration = configuration;
    }


    @Override
    public boolean create(Class clazz) {
        if (TableDescription.isNotEntity(clazz)) {
            return false;
        }

        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(clazz);
        QueryBuilder builder = new QueryBuilder();
        String query = builder.createTable(true)
                .setTableName(tableDescription.getTableName())
                .addColumns(tableDescription)
                .build();
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean save(Object object) {
        if (TableDescription.isNotEntity(object)) {
            return false;
        }

        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(object);
        String tableName = tableDescription.getTableName();
        String questionMarks = tableDescription.getQuestionMarks();

        QueryBuilder queryBuilder = new QueryBuilder();
        String query = queryBuilder.insertInto(tableName, tableDescription.getColumnNames(), questionMarks).build();

        try {
            PreparedStatement statement = connection.prepareStatement(query);
            VeryBadClass.setValues(object, statement);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }


    public boolean update(Object object) {
        if (TableDescription.isNotEntity(object)) {
            return false;
        }
        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(object);
        String tableName = tableDescription.getTableName();

        Object primaryKeyValue = TableDescription.getPrimaryKeyValueFrom(object);

        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder = queryBuilder.update().setTableName(tableName);

        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < tableDescription.getColumnNames().size(); i++) {
            try {
                Field field = fields[i];
                field.setAccessible(true);
                queryBuilder.set(field.getName(), field.get(object));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }

        queryBuilder = queryBuilder.where(tableDescription.getPrimaryKey(), primaryKeyValue);
        String sql = queryBuilder.build();

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }


    @Override
    public boolean delete(Class clazz, Integer id) {
        if (TableDescription.isNotEntity(clazz)) {
            return false;
        }
        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();

        QueryBuilder queryBuilder = new QueryBuilder();
        String query = queryBuilder.deleteFrom()
                .setTableName(tableName)
                .where(tableDescription.getPrimaryKey(), id)
                .build();

        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }


    public boolean dropTable(Class clazz) {

        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();

        QueryBuilder queryBuilder = new QueryBuilder();
        String sql = queryBuilder.dropTable().setTableName(tableName).build();

        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        closeConnection();
        return true;
    }

    @Override
    public <T> List<T> get(Class<T> clazz) {
        Connection connection = openConnection();
        List<T> resultObjects = new ArrayList<>();
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();

        QueryBuilder queryBuilder = new QueryBuilder();
        String sql = queryBuilder.selectAllFrom()
                .setTableName(tableName)
                .build();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                T object = VeryBadClass.getFromResultSet(resultSet, tableDescription, clazz);
                resultObjects.add(object);
            }
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        closeConnection();
        return resultObjects;
    }

    public <T> T get(Class<T> clazz, Integer id) {

        Connection connection = openConnection();
        T object = null;
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();

        QueryBuilder queryBuilder = new QueryBuilder();
        String sql = queryBuilder.selectAllFrom()
                .setTableName(tableName)
                .where(tableDescription.getPrimaryKey(), id)
                .build();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                object = VeryBadClass.getFromResultSet(resultSet, tableDescription, clazz);
            }
        } catch (SQLException | IllegalAccessException e) {
            e.printStackTrace();
        }
        closeConnection();
        return object;
    }


}
