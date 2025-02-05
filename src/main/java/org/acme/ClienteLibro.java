package org.acme;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import org.acme.Modelos.Libro;
import org.acme.Modelos.Prestamo;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/biblioteca/")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "servicio-biblioteca")
public interface ClienteLibro {
    @GET
    @Path("controlador-libro/obtenerLibro")
    Libro obtenerLibro(@QueryParam("libro") long idlibro);

    @POST
    @Path("controlador-libro/guardarLibro")
    Libro guardarLibro(@QueryParam("libro") Libro libro);
}