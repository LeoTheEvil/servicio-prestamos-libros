package org.acme;

import org.acme.Libro
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositorioLibro extends JpaRepository<Libro,Long> {

}