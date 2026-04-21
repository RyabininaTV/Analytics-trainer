package com.example.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/hello")
@Produces(TEXT_PLAIN)
public class DemoResource {

    @GET
    public String hello() {
        return "Hello from Quarkus REST";
    }

}
