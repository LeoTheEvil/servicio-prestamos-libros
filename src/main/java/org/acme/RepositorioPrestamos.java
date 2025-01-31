package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.Modelos.Prestamo;

@ApplicationScoped
public class RepositorioPrestamos implements PanacheRepository<Prestamo> {

}