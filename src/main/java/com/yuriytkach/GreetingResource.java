package com.yuriytkach;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * A simple REST endpoint.
 * GET /hello/{name} - returns a greeting message
 */
@Slf4j
@Path("/hello")
public class GreetingResource {

  @Inject
  StorageService storageService;

  @GET
  @Path("/{name}")
  public Response getKey(
    @PathParam("name") final String name
  ) {
    log.info("Getting key for name: {}", name);
    return storageService.getKey(name)
      .map(ignored -> Response.ok().build())
      .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

  @POST
  @Path("/{name}")
  public Response setKey(
    @PathParam("name") final String name
  ) {
    log.info("Setting key for name: {}", name);
    try {
      storageService.setKey(name);
      return Response.created(new URI("/hello/" + name)).build();
    } catch (final Exception ex) {
      log.error("Failed to set key for name {}: {}", name, ex.getMessage(), ex);
      return Response.serverError().build();
    }
  }

  @DELETE
  @Path("/{name}")
  public Response delKey(
    @PathParam("name") final String name
  ) {
    log.info("Deleting key for name: {}", name);
    return storageService.delKey(name)
      .map(ignored -> Response.noContent().build())
      .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
  }

}
