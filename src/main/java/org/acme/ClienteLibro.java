package org.acme;

import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/biblioteca/")
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "servicio-biblioteca")
public interface ClienteLibro {
    @GET
    @Path("/ObtenerLibro")
    boolean libroPrestado(@QueryParam("libro") Libro libro);
}