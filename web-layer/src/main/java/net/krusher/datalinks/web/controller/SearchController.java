package net.krusher.datalinks.web.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.krusher.datalinks.application.handler.search.SearchCommandHandler;
import net.krusher.datalinks.application.handler.search.TitleSearchCommandHandler;
import net.krusher.datalinks.web.mapper.SearchPaginationCommandMapper;
import net.krusher.datalinks.web.model.SearchPaginationModel;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class SearchController {

    private final TitleSearchCommandHandler titleSearchCommandHandler;
    private final SearchCommandHandler searchCommandHandler;
    private final SearchPaginationCommandMapper searchPaginationCommandMapper;


    @GET
    @Path("titleSearch/{query}")
    public Response titleSearch(@PathParam("query") String query) {
        return Response.ok(titleSearchCommandHandler.handler(query)).build();
    }

    @GET
    @Path("full/{query}")
    public Response search(@PathParam("query") String query,
                           @QueryParam("page") @DefaultValue("0") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        SearchPaginationModel spm = SearchPaginationModel.builder().query(query).page(page).pageSize(pageSize).build();
        return Response.ok(searchCommandHandler.handler(searchPaginationCommandMapper.toCommand(spm))).build();
    }
}
