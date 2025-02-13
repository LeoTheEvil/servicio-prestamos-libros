package org.acme;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.port;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import io.quarkus.test.Mock;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Libro;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
@Alternative()
@Priority(1)
@Mock
@ApplicationScoped
@RegisterRestClient
public class InjectMockTest {

    @InjectMock
    @RestClient
    ClienteLibro mock;
    RecursoBiblioteca biblio = new RecursoBiblioteca();
    Libro libro = new Libro();
    Prestamo prestamo = new Prestamo();

    @BeforeEach
    public void setUp() {
       // when(mock.obtenerLibro(prestamo.id)).thenReturn(libro);
    }

    @Test
    void prestarLibro1aLeo() {
        libro.setId(1L);
        libro.setTitle("Don Quijote de la Mancha");
        libro.setAuthor("Miguel de Cervantes Saavedra");
        libro.setGenre("Comedia");
        prestamo.idLibro=1;
        prestamo.prestatario="Leo";
        biblio.pedirLibro(prestamo);
        int id = given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/api/books").then().statusCode(200)
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
        biblio.pedirLibro(prestamo);
        prestamo.prestatario="Fer";
        biblio.pedirLibro(prestamo);
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
        biblio.pedirLibro(prestamo);
        prestamo.prestatario="Fer";
        biblio.pedirLibro(prestamo);
        prestamo.prestatario = biblio.listaPrestatarios.get(0);
        biblio.listaPrestatarios.remove(0);
        given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
        .accept(MediaType.APPLICATION_JSON.toString()).when().delete("/api/books"+prestamo.id);
        Assertions.assertEquals(true, biblio.listaPrestatarios.isEmpty());
        given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
        .accept(MediaType.APPLICATION_JSON.toString()).when().get("/api/books"+prestamo.id)
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
        biblio.pedirLibro(prestamo);
/*        int id = given().port(port).body(prestamo).contentType(MediaType.APPLICATION_JSON.toString())
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/biblioteca/"+prestamo.idLibro).then().statusCode(200)
                .extract().jsonPath().getObject("idLibro",Integer.class);
*/        given().port(port).when().delete("/api/books"+prestamo.idLibro).then().statusCode(204);
        given().port(port).when().get("/api/books"+prestamo.idLibro).then().statusCode(404);
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
        given().port(port).when().delete("/api/books"+prestamo.idLibro).then().statusCode(404);
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
                .accept(MediaType.APPLICATION_JSON.toString()).when().post("/api/books").then().statusCode(200)
                .extract().jsonPath().getObject("idLibro",Integer.class);
    }
}