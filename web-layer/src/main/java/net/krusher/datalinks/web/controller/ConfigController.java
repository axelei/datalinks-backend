package net.krusher.datalinks.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.krusher.datalinks.application.handler.config.GetConfigCommandHandler;
import net.krusher.datalinks.application.handler.config.GetConfigletCommand;
import net.krusher.datalinks.application.handler.config.GetConfigletCommandHandler;
import net.krusher.datalinks.domain.model.configlet.Configlet;

import java.util.Set;

@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class ConfigController {

    private final GetConfigletCommandHandler getConfigletCommandHandler;
    private final GetConfigCommandHandler getConfigCommandHandler;


    @GET
    @Path("key/{key}")
    public Response get(@PathParam("key") String key) {
        return getConfigletCommandHandler.handler(GetConfigletCommand.builder().key(key).build())
                .map(c -> Response.ok(c).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("all")
    public Set<Configlet> get() {
        return getConfigCommandHandler.handler();
    }

}
