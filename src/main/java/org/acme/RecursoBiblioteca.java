package org.acme;

import jakarta.inject.Inject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Path("/biblioteca")
public class RecursoBiblioteca implements ClienteLibro {

    @Inject
    @RestClient
    ClienteLibro clienteLibro;
    RepositorioLibro repo = new RepositorioLibro();
    Optional<Libro> libro = new Libro();
    Libro libroBuscado;
    List<String> listaPrestatarios;

    @POST
    public void pedirLibro(long idLibro, String nombrePrestatario) {
        libro = repo.findById(idLibro);
        if (libro.isPresent() && this.libroPrestado(clienteLibro.getById(libro))) {
            repo.persist(libro);
        } else {
            System.out.println("Libro no disponible");
            listaPrestatarios.add(nombrePrestatario);
        }
    }

    @DELETE
    public boolean devolverLibro(long idLibro) {
        try {
            libroBuscado = repo.findById(idLibro);
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
        if (listaPrestatarios.isEmpty()) {
            repo.deleteById(libro.id);
        } else {
            libroBuscado.prestatario = listaPrestatarios.get(0);
            listaPrestatarios.remove(0);
        }
        return true;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public boolean libroPrestado(@QueryParam(Libro) Libro libro) {
        try {
            libroBuscado = clienteLibro.getById(libro.id);
            if (libro == null) {
                System.out.println("El libro solicitado no esta en esta biblioteca.");
                return false;
            }
            if  (libroBuscado.id == libro.id) {
                System.out.println("Este libro ya esta prestado a " + libro.prestatario);
                return true;
            } else {return false;}
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            return false;
        }
    }
}