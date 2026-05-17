package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.handler.page.BlockPageCommand;
import net.krusher.datalinks.handler.page.BlockPageCommandHandler;
import net.krusher.datalinks.handler.page.DeletePageCommand;
import net.krusher.datalinks.handler.page.DeletePageCommandHandler;
import net.krusher.datalinks.handler.page.GetContributionsCommandHandler;
import net.krusher.datalinks.handler.page.GetEditCommandHandler;
import net.krusher.datalinks.handler.page.GetPageCommand;
import net.krusher.datalinks.handler.page.GetPageCommandHandler;
import net.krusher.datalinks.handler.page.GetPageShortCommandHandler;
import net.krusher.datalinks.handler.page.GetRandomPageCommandHandler;
import net.krusher.datalinks.handler.page.NewPagesCommandHandler;
import net.krusher.datalinks.handler.page.PageEditsCommandHandler;
import net.krusher.datalinks.handler.page.PostPageCommand;
import net.krusher.datalinks.handler.page.PostPageCommandHandler;
import net.krusher.datalinks.handler.page.RecentChangesCommandHandler;
import net.krusher.datalinks.model.PaginationModel;
import net.krusher.datalinks.model.PostPageModel;
import net.krusher.datalinks.model.page.Edit;

import java.util.List;
import java.util.UUID;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@Path("/page")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PageController {

    private final GetPageCommandHandler getPageCommandHandler;
    private final GetPageShortCommandHandler getPageShortCommandHandler;
    private final PostPageCommandHandler postPageCommandHandler;
    private final NewPagesCommandHandler newPagesCommandHandler;
    private final RecentChangesCommandHandler recentChangesCommandHandler;
    private final GetRandomPageCommandHandler getRandomPageCommandHandler;
    private final DeletePageCommandHandler deletePageCommandHandler;
    private final GetContributionsCommandHandler getContributionsCommandHandler;
    private final PageEditsCommandHandler pageEditsCommandHandler;
    private final GetEditCommandHandler getEditCommandHandler;
    private final BlockPageCommandHandler blockPageCommandHandler;
    private final ObjectMapper objectMapper;

    @Inject
    public PageController(GetPageCommandHandler getPageCommandHandler,
                          GetPageShortCommandHandler getPageShortCommandHandler,
                          PostPageCommandHandler postPageCommandHandler,
                          NewPagesCommandHandler newPagesCommandHandler,
                          RecentChangesCommandHandler recentChangesCommandHandler,
                          GetRandomPageCommandHandler getRandomPageCommandHandler,
                          DeletePageCommandHandler deletePageCommandHandler,
                          PageEditsCommandHandler pageEditsCommandHandler,
                          GetContributionsCommandHandler getContributionsCommandHandler,
                          GetEditCommandHandler getEditCommandHandler,
                          BlockPageCommandHandler blockPageCommandHandler,
                          ObjectMapper objectMapper) {
        this.getPageCommandHandler = getPageCommandHandler;
        this.getPageShortCommandHandler = getPageShortCommandHandler;
        this.postPageCommandHandler = postPageCommandHandler;
        this.newPagesCommandHandler = newPagesCommandHandler;
        this.recentChangesCommandHandler = recentChangesCommandHandler;
        this.getRandomPageCommandHandler = getRandomPageCommandHandler;
        this.deletePageCommandHandler = deletePageCommandHandler;
        this.getContributionsCommandHandler = getContributionsCommandHandler;
        this.pageEditsCommandHandler = pageEditsCommandHandler;
        this.getEditCommandHandler = getEditCommandHandler;
        this.blockPageCommandHandler = blockPageCommandHandler;
        this.objectMapper = objectMapper;
    }

    private static String remoteAddr(RoutingContext rc) {
        if (rc == null || rc.request().remoteAddress() == null) {
            return null;
        }
        return rc.request().remoteAddress().host();
    }

    @GET
    @Path("{title}")
    public Response get(@PathParam("title") String title, @HeaderParam(AUTH_HEADER) String userToken) {
        return Try.of(() -> getPageCommandHandler.handler(
                        GetPageCommand.builder()
                                .title(title)
                                .loginTokenId(toLoginToken(userToken))
                                .build()))
                .map(optionalResult -> optionalResult
                        .map(p -> Response.ok(p).build())
                        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build()))
                .recover(EngineException.class, e ->
                        e.getErrorType().equals(ErrorType.PERMISSIONS_ERROR)
                                ? Response.status(403).build()
                                : Response.status(500).build())
                .get();
    }

    @GET
    @Path("-short/{title}")
    public Response getShort(@PathParam("title") String title, @HeaderParam(AUTH_HEADER) String userToken) {
        return Try.of(() -> getPageShortCommandHandler.handler(
                        GetPageCommand.builder()
                                .title(title)
                                .loginTokenId(toLoginToken(userToken))
                                .build()))
                .map(optionalResult -> optionalResult
                        .map(p -> Response.ok(p).build())
                        .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build()))
                .recover(EngineException.class, e ->
                        e.getErrorType().equals(ErrorType.PERMISSIONS_ERROR)
                                ? Response.status(403).build()
                                : Response.status(500).build())
                .get();
    }

    @GET
    @Path("-edits/{title}")
    public Response edits(@PathParam("title") String title,
                          @QueryParam("page") @DefaultValue("0") int page,
                          @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<Edit> result = pageEditsCommandHandler.handler(SearchPaginationCommand.builder()
                .query(title)
                .page(page)
                .pageSize(pageSize)
                .build());
        return Response.ok(result).build();
    }

    @GET
    @Path("-randomPage")
    public Response random() {
        return getRandomPageCommandHandler.handler()
                .map(p -> Response.ok(p).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("-contributions/{username}")
    public Response contributions(@PathParam("username") String username,
                                  @QueryParam("page") @DefaultValue("0") int page,
                                  @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        return Response.ok(getContributionsCommandHandler.handler(SearchPaginationCommand.builder()
                .query(username)
                .page(page)
                .pageSize(pageSize)
                .build())).build();
    }

    @DELETE
    @Path("{title}")
    public void delete(@PathParam("title") String title, @HeaderParam(AUTH_HEADER) String userToken) {
        deletePageCommandHandler.handler(DeletePageCommand.builder()
                .title(title)
                .loginTokenId(toLoginToken(userToken))
                .build());
    }

    @POST
    @Path("block/{title}")
    public Response block(@PathParam("title") String title,
                          @QueryParam("readBlock") @DefaultValue("0") int readBlock,
                          @QueryParam("writeBlock") @DefaultValue("0") int writeBlock,
                          @HeaderParam(AUTH_HEADER) String userToken) {
        blockPageCommandHandler.handler(BlockPageCommand.builder()
                .title(title)
                .readBlock(readBlock)
                .writeBlock(writeBlock)
                .loginToken(toLoginToken(userToken))
                .build());
        return Response.ok("OK").build();
    }

    @PUT
    @Path("{title}")
    @Consumes(MediaType.WILDCARD)
    public void put(@PathParam("title") String title,
                    String body,
                    @HeaderParam(AUTH_HEADER) String userToken,
                    @Context RoutingContext routingContext) throws JsonProcessingException {
        PostPageModel postPageModel = objectMapper.readValue(body, PostPageModel.class);
        postPageCommandHandler.handler(PostPageCommand.builder()
                .title(title)
                .content(postPageModel.getContent())
                .categories(postPageModel.getCategories())
                .loginTokenId(toLoginToken(userToken))
                .ip(remoteAddr(routingContext))
                .build());
    }

    @POST
    @Path("newPages")
    @Consumes(MediaType.WILDCARD)
    public Response newPages(String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        return Response.ok(newPagesCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build())).build();
    }

    @POST
    @Path("recentChanges")
    @Consumes(MediaType.WILDCARD)
    public Response recentChanges(String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        return Response.ok(recentChangesCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build())).build();
    }

    @GET
    @Path("-edit/{id}")
    public Response edit(@PathParam("id") String id) {
        return getEditCommandHandler.handler(UUID.fromString(id))
                .map(e -> Response.ok(e).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }
}
