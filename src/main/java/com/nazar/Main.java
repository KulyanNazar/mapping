package com.nazar;

import com.nazar.entities.Gr;
import com.nazar.entityMenager.EntityManager;
import com.nazar.entityMenager.EntityManagerConfiguration;

public class Main {
    
    //triger 
    public static void main(String[] args) {
        EntityManagerConfiguration configuration = new EntityManagerConfiguration.EntityManagerConfigurationBuilder()
                .setEntityPackage("com.nazar.entities")
                .setURL("jdbc:mysql://localhost:3306/university")
                .setUser("root")
                .setPassword("root")
                .build();

        EntityManager entityManager = new EntityManager(configuration);
        //Student student = new Student("cccc", 8);
        Gr gr = new Gr("aaddaaaa",1);
        entityManager.save(gr);
    }
}
