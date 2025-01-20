package org.acme;

import org.acme.Libro
import java.util.List;

public interface ServicioLibro {
    Libro pedirLibro(Libro libro);
    Libro obtenerLibro(Long idLibro);
    Libro libroPrestado(Long idLibro);
    boolean devolverLibro(Long idLibro);
}