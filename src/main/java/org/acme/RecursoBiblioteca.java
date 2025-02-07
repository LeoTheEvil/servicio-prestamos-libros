package org.acme;

import jakarta.inject.Inject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Libro;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/biblioteca")
public class RecursoBiblioteca {

    @Inject
    @RestClient
    ClienteLibro clienteLibro;
    RepositorioPrestamos repo = new RepositorioPrestamos();
    List<String> listaPrestatarios;

    @POST
    public void pedirLibro(long idLibro, String nombrePrestatario) {
        Prestamo prestamo = new Prestamo();
        int codigo = libroPrestado(idLibro);
        if (codigo == 202) {
            System.out.println("Libro no disponible");
            listaPrestatarios.add(nombrePrestatario);
        } else if (codigo == 200) {
            prestamo.id=idLibro;
            prestamo.prestatario=nombrePrestatario;
            repo.persist(prestamo);
        }
    }

    @DELETE
    public boolean devolverLibro(long idPrestamo) {
        Prestamo prestamo = new Prestamo();
        try {
            prestamo = repo.findById(idPrestamo);
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
    @Path("/{idLibro}")
    public int libroPrestado(@PathParam("IdLibro") long idPrestamo) {
        Libro libroBuscado;
        try {
            libroBuscado = clienteLibro.obtenerLibro(idPrestamo);
            if (libroBuscado.getId() == 0) {
                System.out.println("El libro solicitado no esta en esta biblioteca.");
                return 404;
            }
            Prestamo prestamo = repo.findById(idPrestamo);
            if  (prestamo != null) {
                System.out.println("Este libro ya esta prestado a " + prestamo.prestatario);
                return 202;
            } else {return 200;}
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            return 500;
        }
    }
}