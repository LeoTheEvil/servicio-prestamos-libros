package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Libro;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "servicio-biblioteca")
@ApplicationScoped
public interface ClienteLibro {
    @GET
    @Path("controlador-libro/obtenerLibro")
    Libro obtenerLibro(@QueryParam("libro") long idlibro);

    @POST
    @Path("controlador-libro/guardarLibro")
    Libro guardarLibro(@QueryParam("libro") Libro libro);
}