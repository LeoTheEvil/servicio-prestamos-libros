package org.acme;

import org.acme.Libro;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.ws.rs.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Path("/biblioteca")
public class RecursoBiblioteca {
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    RepositorioLibro repo = new RepositorioLibro();
    Libro libro = new Libro();
    Libro libroBuscado;
    List<String> listaPrestatarios;

    @POST
    public void pedirLibro(long idLibro, String nombrePrestatario) {
        libro = repo.findById(idLibro);
        if (libro != null && this.libroPrestado(libro)) {
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
    public boolean libroPrestado(Libro libro) {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8081/api/books"))
                    .header("Content-Type", "application/json")
                    .header("accept", "*/*")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            libroBuscado = repo.findById(libro.id);
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