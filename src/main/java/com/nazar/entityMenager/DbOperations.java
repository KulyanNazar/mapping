package com.nazar.entityMenager;

import java.util.List;

public interface DbOperations {
    boolean create(Class object);

    boolean save(Object object);

    boolean update(Object object);

    boolean delete(Class clazz, Integer id);

    <T> T get(Class<T> clazz, Integer id);

    <T> List<T> get(Class<T> clazz);

    public boolean dropTable(Class clazz);
}
