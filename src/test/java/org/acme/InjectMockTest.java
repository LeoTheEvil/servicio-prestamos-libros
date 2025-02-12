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
        .accept(MediaType.APPLICATION_JSON.toString()).when().delete("/biblioteca/"+prestamo.id);
        given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
        .accept(MediaType.APPLICATION_JSON.toString()).when().get("/biblioteca/"+prestamo.id)
        .then().body("prestatario", equalTo("Fer"));
    }
    @Test
    @Transactional
    void devolverLibro1() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        prestamo.idLibro=1;
        prestamo.prestatario="Leo";
        repo.persist(prestamo);
        int id = given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/biblioteca/"+prestamo.idLibro).then().statusCode(200)
                .extract().jsonPath().getObject("idLibro",Integer.class);
        given().port(port).when().delete("/biblioteca/"+id).then().statusCode(204);
        given().port(port).when().get("/biblioteca/"+id).then().statusCode(404);
    }

    @Test
    void pedirLibroInexistente() {
        libro.setId(13L);
        libro.setTitle("El Rey de Amarillo");
        libro.setAuthor("Robert William Chambers");
        libro.setGenre("Terror");
        prestamo.idLibro=13;
        prestamo.prestatario="Leo";

    }

    @Test
    void devolverLibroInexistente() {
        prestamo.idLibro=13;
        prestamo.prestatario="Leo";
        given().port(port).when().delete("/biblioteca/"+prestamo.idLibro).then().statusCode(404);
    }

    @Test
    void idInvalido() {
//        libro.setId();
        libro.setTitle("El Rey de Amarillo");
        libro.setAuthor("Robert William Chambers");
        libro.setGenre("Terror");
//        prestamo.idLibro=;
        prestamo.prestatario="Leo";
        int id = given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/biblioteca/").then().statusCode(200)
                .extract().jsonPath().getObject("idLibro",Integer.class);
    }
}