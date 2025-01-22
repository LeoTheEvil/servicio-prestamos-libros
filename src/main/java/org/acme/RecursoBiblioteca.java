package org.acme;

import org.acme.Libro;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.ws.rs.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Path("/biblioteca")
public class RecursoBiblioteca {
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    PanacheEntity libroBuscado;
    RepositorioLibro repo = new RepositorioLibro();
    Libro libro = new Libro();

    @POST
    public void pedirLibro(long idLibro) {
        libro = repo.findById(idLibro);
        if (libro != null && this.libroPrestado(idLibro)) {
            repo.persist(libro);
        } else {
            System.out.println("Libro no disponible");
        }
    }

    @GET
    public Libro obtenerLibro(long idLibro) {
        libroBuscado = repo.findById(idLibro);
        if (libroBuscado == null) {throw new ExcepcionNoEncuentraLibro();}
        return libro;
    }

    @DELETE
    public boolean devolverLibro(long idLibro) {
        try {
            this.obtenerLibro(idLibro);
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
        repo.deleteById(libro.id);
        return true;
    }

    @GET
    public boolean libroPrestado(long idLibro) {
        try {
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8081/api/books"))
                    .header("Content-Type", "application/json")
                    .header("accept", "*/*")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return true;
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            return false;
        }
    }
}