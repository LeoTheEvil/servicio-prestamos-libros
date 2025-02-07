package org.acme.Modelos;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class Prestamo extends PanacheEntity{
    public long idLibro;
    public String prestatario;
}