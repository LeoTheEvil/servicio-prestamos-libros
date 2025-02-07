package org.acme;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Libro;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class InjectMockTest {

    @InjectMock
    @RestClient
    ClienteLibro mock;

    RepositorioPrestamos repo = new RepositorioPrestamos();
    RecursoBiblioteca biblio = new RecursoBiblioteca();
    Libro libro = new Libro();

    @Test
    void prestarLibro1aLeo() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        long idPrestamo=1;
        String nombrePrestatario="Leo";
        Prestamo prestamo = biblio.pedirLibro(idPrestamo, nombrePrestatario);
        when(mock.obtenerLibro(idPrestamo)).thenReturn(libro);
        given().port(port).contentType(MediaType.APPLICATION_JSON).when().get("/biblioteca/"+libro.getId()).then().statusCode(200).body("id", equalTo(idPrestamo));
        Assertions.assertEquals(idPrestamo, repo.findById(1L));
    }
}