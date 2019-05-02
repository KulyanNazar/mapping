package com.nazar.entityMenager;

import com.nazar.annotations.Column;
import com.nazar.annotations.Entity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class EntityManager implements CrudOperations {
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

    public void prepareForSaving() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = configuration.getEntityPackage().replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            File packagePath = new File(resource.getFile());
            List<Class> classes = getAllClasses(packagePath);

        }
    }

    private List<Class> getAllClasses(File packagePath) {
        List<Class> classes = new ArrayList<>();
        File[] files = packagePath.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                System.out.println(file);
                try {
                    String fileName = retrieveRealClassNameFromFilePath(file);
                    if (!fileName.equals("")) {
                        Class clazz = Class.forName(configuration.getEntityPackage() + "." + fileName);
                        classes.add(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    private String retrieveRealClassNameFromFilePath(File file) {
        String classFormat = ".class";
        String fileName = "";
        if (file.getName().contains(classFormat)) {
            fileName = file.getName().substring(0, file.getName().length() - 6);
        }
        return fileName;
    }

    /*private List<Class> getAllEntities(){
        File directory
    }*/


    private boolean isEntity(Object object) {
        boolean isEntity = false;
        Class clazz = object.getClass();
        if (clazz.isAnnotationPresent(Entity.class)) {
            isEntity = true;
        }
        return isEntity;
    }


   /* private TableDescription getColumnNames(Object object, TableDescription tableDescription){
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                field.setAccessible(true);
                tableDescription.addColumnName(field.getName());
            }
        }
        return tableDescription;
    }*/

    @Override
    public boolean create(Object object) {
        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(object);

        String row = "";
        ArrayList<String> columnList = new ArrayList<>();
        for (String columnName : tableDescription.getColumnNames()) {
            row = columnName + " " + defineSqlType(tableDescription, columnName);
            columnList.add(row);
        }
        String columns = String.join(",", columnList);
        String query = "CREATE TABLE IF NOT EXISTS " + tableDescription.getTableName() + " (" +
                columns + ", "
                + "PRIMARY KEY (" + tableDescription.getPrimaryKey() + ")"


                + ");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    private String defineSqlType(TableDescription tableDescription, String columnName) {
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

    public boolean save(Object object) {
        if (!isEntity(object)) {
            return false;
        }
        Connection connection = openConnection();

        TableDescription tableDescription = new TableDescription(object);
        String tableName = tableDescription.getTableName();
        String columnNames = String.join(",", tableDescription.getColumnNames());
        String questionMarks = tableDescription.getQuestionMarks();
        String query = "INSERT INTO " + tableName + "(" + columnNames + ")" + " values " + "(" + questionMarks + ")";

        System.out.println(query);

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }


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

        try {
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return true;
    }

    public boolean uptate() {
        return false;
    }


    @Override
    public boolean delete(Class clazz, Integer id) {
        Connection connection = openConnection();
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();
        String query = "DELETE FROM " + tableName + " where id = " + id;
        try {
            statement.execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return true;
    }

    public boolean dropTable(Class clazz) {
        Connection connection = openConnection();
        Statement statement = null;
        TableDescription tableDescription = new TableDescription(clazz);
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String tableName = tableDescription.getTableName();
        String sql = "DROP TABLE " + tableName;
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeConnection();
        return true;
    }

    public boolean get(Class clazz, Integer id) {
        Connection connection = openConnection();
        TableDescription tableDescription = new TableDescription(clazz);
        String tableName = tableDescription.getTableName();
        String sql = "SELECT * FROM " + tableName + " where id = " + id;
        Statement statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ResultSet resultSet = statement.executeQuery(sql);
            int counter = 0;
            Map<String, TableDescription.ColumnDescription> map = tableDescription.getColums();
            Set<String> set = map.keySet();
            while (resultSet.next()){

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
        return false;
    }
}
