package com.nazar.entities;

import com.nazar.annotations.Column;
import com.nazar.annotations.Entity;
import com.nazar.annotations.Id;

@Entity
public class Student {
    @Id
    private int id;

    @Column
    private String name;

    @Column
    private int age;

    public Student() {
    }

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
