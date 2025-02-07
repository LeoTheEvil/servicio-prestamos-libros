package org.acme;

import jakarta.inject.Inject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
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
        Response respuesta = libroPrestado(idLibro);
        if (respuesta.getStatus() == 202) {
            System.out.println("Libro no disponible");
            listaPrestatarios.add(nombrePrestatario);
        } else if (respuesta.getStatus() == 200) {
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


    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @Path("/{idLibro}")
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