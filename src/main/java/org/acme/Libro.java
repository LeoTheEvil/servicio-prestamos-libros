package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Libro extends PanacheEntity{
    public long id;
    public String prestatario;
}