package net.krusher.datalinks.controller;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.krusher.datalinks.handler.category.CreateCategoryCommandHandler;
import net.krusher.datalinks.handler.category.DeleteCategoryCommandHandler;
import net.krusher.datalinks.handler.category.FindCategoriesCommandHandler;
import net.krusher.datalinks.handler.category.FindCategoryPagesCommandHandler;
import net.krusher.datalinks.handler.category.GetCategoriesCommandHandler;
import net.krusher.datalinks.handler.category.GetCategoryCommandHandler;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.PageShort;

import java.util.List;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@Path("/category")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {

    private final GetCategoriesCommandHandler getCategoriesCommandHandler;
    private final DeleteCategoryCommandHandler deleteCategoryCommandHandler;
    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final GetCategoryCommandHandler getCategoryCommandHandler;
    private final FindCategoriesCommandHandler findCategoriesCommandHandler;
    private final FindCategoryPagesCommandHandler findCategoryPagesCommandHandler;

    @Inject
    public CategoryController(GetCategoriesCommandHandler getCategoriesCommandHandler,
                              DeleteCategoryCommandHandler deleteCategoryCommandHandler,
                              CreateCategoryCommandHandler createCategoryCommandHandler,
                              GetCategoryCommandHandler getCategoryCommandHandler,
                              FindCategoriesCommandHandler findCategoriesCommandHandler,
                              FindCategoryPagesCommandHandler findCategoryPagesCommandHandler) {
        this.getCategoriesCommandHandler = getCategoriesCommandHandler;
        this.deleteCategoryCommandHandler = deleteCategoryCommandHandler;
        this.createCategoryCommandHandler = createCategoryCommandHandler;
        this.getCategoryCommandHandler = getCategoryCommandHandler;
        this.findCategoriesCommandHandler = findCategoriesCommandHandler;
        this.findCategoryPagesCommandHandler = findCategoryPagesCommandHandler;
    }

    @GET
    @Path("all")
    public Response getAll(@QueryParam("page") @DefaultValue("0") int page,
                           @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<Category> categories = getCategoriesCommandHandler.handler(PaginationCommand.builder()
                .page(page)
                .pageSize(pageSize)
                .build());
        return Response.ok(categories).build();
    }

    @DELETE
    @Path("delete/{name}")
    public Response delete(@PathParam("name") String name,
                           @HeaderParam(AUTH_HEADER) String userToken) {
        deleteCategoryCommandHandler.handler(name, toLoginToken(userToken));
        return Response.ok("OK").build();
    }

    @PUT
    @Path("add")
    @Consumes(MediaType.WILDCARD)
    public Response update(String name,
                           @HeaderParam(AUTH_HEADER) String userToken) {
        createCategoryCommandHandler.handler(name, toLoginToken(userToken));
        return Response.ok("OK").build();
    }

    @GET
    @Path("get/{name}")
    public Response get(@PathParam("name") String name) {
        return getCategoryCommandHandler.handler(name)
                .map(c -> Response.ok(c).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("find/{query}")
    public Response find(@PathParam("query") String query) {
        return Response.ok(findCategoriesCommandHandler.handler(query)).build();
    }

    @GET
    @Path("findPages/{query}")
    public Response findPages(@PathParam("query") String query,
                              @QueryParam("page") @DefaultValue("0") int page,
                              @QueryParam("pageSize") @DefaultValue("10") int pageSize) {
        List<PageShort> pages = findCategoryPagesCommandHandler.handler(SearchPaginationCommand.builder()
                .query(query)
                .page(page)
                .pageSize(pageSize)
                .build());
        return Response.ok(pages).build();
    }
}
