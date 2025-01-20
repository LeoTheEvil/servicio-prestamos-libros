package org.acme;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.Libro;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Path("/biblioteca")
public class RecursoBiblioteca implements ServicioLibro {

    private final RepositorioLibro repositorioLibro;
    ObjectMapper objectMapper = new ObjectMapper();
    Libro libro = new Libro();
    Libro libroBuscado;
    HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();
    int codigo;

    @POST
    public Libro pedirLibro(Libro libro) {
        if (this.libroPrestado(libro.id) == true) {
            return repositorioLibro.save(libro);
        } else {return null;}
    }

    @GET
    public Libro obtenerLibro(long idLibro) {
        return repositorioLibro.findById(idLibro).orElseThrow(() -> {throw new ExcepcionNoEncuentraLibro();});
    }

    @DELETE
    public boolean devolverLibro(long idLibro) {
        try {
            this.obtenerLibro(idLibro);
        } catch (ExcepcionNoEncuentraLibro e) {
            return false;
        }
        repositorioLibro.deleteById(idLibro);
        return true;
    }

    @GET
    public boolean libroPrestado(long idLibro) {
        try {
            String json = objectMapper.writeValueAsString(libro);
            HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8081/api/books"))
                    .header("Content-Type", "application/json")
                    .header("accept", "*/*")
                    .GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            libroBuscado = this.obtenerLibro(libro.id);
            if (libro.id == libroBuscado.id){return true;} else {return false;}
        } catch (Exception ConnectionError) {
            ConnectionError.getMessage();
            return false;
        }
    }
}