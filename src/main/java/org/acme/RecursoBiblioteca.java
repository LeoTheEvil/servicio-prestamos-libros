package org.acme;

import jakarta.inject.Inject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/biblioteca")
public class RecursoBiblioteca {

    @Inject
    @RestClient
    ClienteLibro clienteLibro;
    RepositorioPrestamos repo = new RepositorioPrestamos();
    Prestamo prestamo = new Prestamo();
    long idLibroBuscado;
    List<String> listaPrestatarios;
    int codigo;

    @POST
    public void pedirLibro(Prestamo prestamo) {
        libroPrestado(prestamo.id);
        if (codigo == 200) {
            repo.persist(prestamo);
        } else if (codigo == 202) {
            System.out.println("Libro no disponible");
            listaPrestatarios.add(prestamo.prestatario);
        }
    }

    @DELETE
    public boolean devolverLibro(long idPrestamo) {
        try {
            prestamo = repo.findById(prestamo.id);
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
        if (listaPrestatarios.isEmpty()) {
            repo.deleteById(prestamo.id);
        } else {
            prestamo.prestatario = listaPrestatarios.get(0);
            listaPrestatarios.remove(0);
        }
        return true;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public int libroPrestado(long idlibro) {
        try {
            idLibroBuscado = clienteLibro.obtenerLibro(idlibro);
            if (idLibroBuscado == 0) {
                System.out.println("El libro solicitado no esta en esta biblioteca.");
                codigo=404;
            }
            if  (prestamo.id == idLibroBuscado) {
                System.out.println("Este libro ya esta prestado a " + prestamo.prestatario);
                codigo=202;
            } else {codigo=200;}
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            codigo=500;
        }
        return codigo;
    }
}