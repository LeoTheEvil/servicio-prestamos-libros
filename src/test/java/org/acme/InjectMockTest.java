package org.acme;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import jakarta.transaction.Transactional;
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
    Prestamo prestamo = new Prestamo();

    @Test
    void prestarLibro1aLeo() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        prestamo.idLibro=1;
        prestamo.prestatario="Leo";
        int id = given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/biblioteca/").then().statusCode(200)
                .extract().jsonPath().getObject("idLibro",Integer.class);
    }

    @Test
    @Transactional
    void poneraFerEnListaDeEspera() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        prestamo.idLibro=1;
        prestamo.prestatario="Leo";
        repo.persist(prestamo);
        prestamo.prestatario="Fer";
        biblio.listaPrestatarios.add(prestamo.prestatario);
        Assertions.assertEquals("Fer", biblio.listaPrestatarios.get(0));
    }

    @Test
    @Transactional
    void pasarLibro1aFer() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        prestamo.idLibro=1;
        prestamo.prestatario="Leo";
        repo.persist(prestamo);
        prestamo.prestatario="Fer";
        biblio.listaPrestatarios.add(prestamo.prestatario);
        prestamo.prestatario = biblio.listaPrestatarios.get(0);
        biblio.listaPrestatarios.remove(0);
        repo.persist(prestamo);
        Assertions.assertEquals(true, biblio.listaPrestatarios.isEmpty());
        given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
        .accept(MediaType.APPLICATION_JSON.toString()).when().get("/biblioteca/"+prestamo.id)
        .then().body("prestatario", equalTo("Fer"));
    }
    @Test
    void devolverLibro1() {

    }
}