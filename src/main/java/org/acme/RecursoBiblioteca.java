package org.acme;

import jakarta.inject.Inject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.Modelos.Libro;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@Path("/biblioteca")
public class RecursoBiblioteca {

    @Inject
    @RestClient
    ClienteLibro clienteLibro;
    RepositorioPrestamos repo = new RepositorioPrestamos();
    List<String> listaPrestatarios = new ArrayList<>();

    @POST
    @Path("/{idPrestamo}")
    public Prestamo pedirLibro(Prestamo prestamo) {
        Response respuesta = libroPrestado(prestamo.idLibro);
        if (respuesta.getStatus() == 202) {
            System.out.println("Libro no disponible");
            listaPrestatarios.add(prestamo.prestatario);
        } else if (respuesta.getStatus() == 200) {
            repo.persist(prestamo);
        }
        return prestamo;
    }

    @DELETE
    @Path("/{idPrestamo}")
    public boolean devolverLibro(@PathParam("idPrestamo") long idPrestamo) {
        try {
            Prestamo prestamo = repo.findById(idPrestamo);
            if (listaPrestatarios.isEmpty()) {
                repo.deleteById(prestamo.id);
            } else {
                prestamo.prestatario = listaPrestatarios.get(0);
                listaPrestatarios.remove(0);
            }
            return true;
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
    }

    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{idPrestamo}")
    public Response libroPrestado(@PathParam("idPrestamo") long idPrestamo) {
        Libro libroBuscado;
        try {
            libroBuscado = clienteLibro.obtenerLibro(idPrestamo);
            if (libroBuscado.getId() == 0) {
                System.out.println("El libro solicitado no esta en esta biblioteca.");
                return Response.noContent().build();
            }
            Prestamo prestamo = repo.findById(idPrestamo);
            if  (prestamo != null) {
                System.out.println("Este libro ya esta prestado a " + prestamo.prestatario);
                return Response.accepted().build();
            } else {return Response.ok().build();}
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            return Response.serverError().build();
        }
    }
}