package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.ws.rs.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.sql.DriverManager.println;

@Path("/biblioteca")
public class RecursoBiblioteca {
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    PanacheEntity libroBuscado;

    @POST
    public void pedirLibro(long idLibro) {
        if (this.libroPrestado(idLibro)) {
            RepositorioLibro.persist(idLibro);
        } else {println("Libro no disponible");}
    }

    @GET
    public long obtenerLibro(long idLibro) {
        libroBuscado = RepositorioLibro.findById(idLibro);
        if (libroBuscado == null) {throw new ExcepcionNoEncuentraLibro();}
        return idLibro;
    }

    @DELETE
    public boolean devolverLibro(long idLibro) {
        try {
            this.obtenerLibro(idLibro);
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
        RepositorioLibro.deleteById(idLibro);
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