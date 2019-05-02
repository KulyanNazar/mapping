package com.nazar.entities;

import com.nazar.annotations.Column;
import com.nazar.annotations.Entity;
import com.nazar.annotations.Id;

@Entity
public class Gr {
    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private Integer quantity;

    public Gr(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
