package com.nazar.entityMenager;

public interface CrudOperations {
    boolean create(Object object);

    boolean save(Object object);

    boolean uptate();

    boolean delete(Class clazz, Integer id);

    boolean get(Class clazz, Integer id);
}
