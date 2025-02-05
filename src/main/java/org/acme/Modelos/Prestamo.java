package org.acme.Modelos;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;

@Entity
public class Prestamo extends PanacheEntity{
    public long id;
    public String prestatario;
    public boolean prestado;
}