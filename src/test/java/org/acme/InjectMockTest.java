package org.acme;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.util.Optional;
import jakarta.ws.rs.client.Client;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class InjectMockTest {

    @InjectMock
    @RestClient
    @ConfigProperty(name = "http://localhost:8081/api/books")
    ClienteLibro mock;

    RepositorioPrestamos repo = new RepositorioPrestamos();
    RecursoBiblioteca biblio = new RecursoBiblioteca();
    Prestamo prestamo = new Prestamo();

    @Test
    void prestarLibro1aLeo() {
        prestamo.id=1;
        prestamo.prestatario="Leo";
        given().port(port).when().get("/biblioteca"+prestamo.id).then().body("id", equalTo(prestamo.id));
        when(biblio.pedirLibro(prestamo)).thenReturn(repo.findById(1L));
        Assertions.assertEquals(prestamo, repo.findById(1L));
    }
}