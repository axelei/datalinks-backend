package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.upload.DeleteUploadCommand;
import net.krusher.datalinks.handler.upload.DeleteUploadCommandHandler;
import net.krusher.datalinks.handler.upload.FindUsagesCommandHandler;
import net.krusher.datalinks.handler.upload.GetFileCommand;
import net.krusher.datalinks.handler.upload.GetFileCommandHandler;
import net.krusher.datalinks.handler.upload.NewUploadsCommandHandler;
import net.krusher.datalinks.handler.upload.UpdateUploadCommand;
import net.krusher.datalinks.handler.upload.UpdateUploadCommandHandler;
import net.krusher.datalinks.handler.upload.UploadCommand;
import net.krusher.datalinks.handler.upload.UploadCommandHandler;
import net.krusher.datalinks.model.FileTypes;
import net.krusher.datalinks.model.PaginationModel;
import net.krusher.datalinks.model.UpdateUploadModel;
import net.krusher.datalinks.model.UploadResponse;
import net.krusher.datalinks.model.upload.Upload;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@Path("/file")
@Produces(MediaType.APPLICATION_JSON)
public class UploadController {

    private final ObjectMapper objectMapper;
    private final UploadCommandHandler uploadCommandHandler;
    private final UpdateUploadCommandHandler updateUploadCommandHandler;
    private final GetFileCommandHandler getFileCommandHandler;
    private final NewUploadsCommandHandler newUploadsCommandHandler;
    private final FindUsagesCommandHandler findUsagesCommandHandler;
    private final DeleteUploadCommandHandler deleteUploadCommandHandler;

    @Inject
    public UploadController(ObjectMapper objectMapper,
                            UploadCommandHandler uploadCommandHandler,
                            UpdateUploadCommandHandler updateUploadCommandHandler,
                            GetFileCommandHandler getFileCommandHandler,
                            NewUploadsCommandHandler newUploadsCommandHandler,
                            FindUsagesCommandHandler findUsagesCommandHandler,
                            DeleteUploadCommandHandler deleteUploadCommandHandler) {
        this.uploadCommandHandler = uploadCommandHandler;
        this.updateUploadCommandHandler = updateUploadCommandHandler;
        this.objectMapper = objectMapper;
        this.getFileCommandHandler = getFileCommandHandler;
        this.newUploadsCommandHandler = newUploadsCommandHandler;
        this.findUsagesCommandHandler = findUsagesCommandHandler;
        this.deleteUploadCommandHandler = deleteUploadCommandHandler;
    }

    private static String remoteAddr(RoutingContext rc) {
        if (rc == null || rc.request().remoteAddress() == null) {
            return null;
        }
        return rc.request().remoteAddress().host();
    }

    @GET
    @Path("/lookAt/{filename}")
    public Response lookAt(@PathParam("filename") String filename, @HeaderParam(AUTH_HEADER) String userToken) {
        Upload upload = getFileCommandHandler.handler(GetFileCommand.builder()
                .filename(filename)
                .loginTokenId(toLoginToken(userToken))
                .build());
        upload.setInputStream(null);
        return Response.ok(upload).build();
    }

    @GET
    @Path("/get/{filename}")
    public Response get(@PathParam("filename") String filename, @HeaderParam(AUTH_HEADER) String userToken) {

        Upload upload = Try.of(() -> getFileCommandHandler.handler(
                        GetFileCommand.builder()
                                .filename(filename)
                                .loginTokenId(toLoginToken(userToken))
                                .build()))
                .recover(EngineException.class, e -> {
                    if (ErrorType.FILE_NOT_FOUND.equals(e.getErrorType())) {
                        return null;
                    }
                    throw e;
                })
                .get();

        if (upload == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        InputStream inputStream = Option.of(upload.getInputStream())
                .getOrElse(() -> getClass().getResourceAsStream("/image-not-found-icon.svg"));
        MediaType mediaType = Option.of(upload.getInputStream())
                .map(__ -> getMediaType(upload.getFilename()))
                .getOrElse(FileTypes.SVG.getMediaType());

        CacheControl cc = new CacheControl();
        cc.setMaxAge(3600);
        cc.setMustRevalidate(true);

        return Response.ok(inputStream)
                .type(mediaType)
                .cacheControl(cc)
                .build();
    }

    private MediaType getMediaType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return FileTypes.valueOf(extension.toUpperCase()).getMediaType();
    }

    @DELETE
    @Path("/delete/{filename}")
    public Response delete(@PathParam("filename") String title, @HeaderParam(AUTH_HEADER) String userToken) {
        deleteUploadCommandHandler.handler(DeleteUploadCommand.builder()
                .filename(title)
                .loginToken(toLoginToken(userToken))
                .build());
        return Response.ok("OK").build();
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response put(
            @HeaderParam(AUTH_HEADER) String userToken,
            @RestForm("upload") FileUpload upload,
            @RestForm("description") @org.jboss.resteasy.reactive.PartType(MediaType.TEXT_PLAIN) String description,
            @Context RoutingContext routingContext) throws IOException {
        if (upload == null || upload.fileName() == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        String fileName = upload.fileName();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        if (Arrays.stream(FileTypes.values()).noneMatch(fileType -> fileType.name().equalsIgnoreCase(extension))) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        try (InputStream is = Files.newInputStream(upload.uploadedFile())) {
            String url = uploadCommandHandler.handler(UploadCommand.builder()
                    .inputStream(is)
                    .loginTokenId(toLoginToken(userToken))
                    .filename(fileName)
                    .description(description == null ? "" : description)
                    .ip(remoteAddr(routingContext))
                    .build());
            return Response.ok(UploadResponse.builder().url(url).build()).build();
        }
    }

    @PUT
    @Path("/update")
    @Consumes(MediaType.WILDCARD)
    public Response update(@HeaderParam(AUTH_HEADER) String userToken,
                           String body,
                           @Context RoutingContext routingContext) throws JsonProcessingException {
        UpdateUploadModel paginationModel = objectMapper.readValue(body, UpdateUploadModel.class);
        updateUploadCommandHandler.handler(UpdateUploadCommand.builder()
                .loginToken(toLoginToken(userToken))
                .description(paginationModel.getDescription())
                .filename(paginationModel.getFilename())
                .ip(remoteAddr(routingContext))
                .build());
        return Response.ok("ok").build();
    }

    @POST
    @Path("newUploads")
    @Consumes(MediaType.WILDCARD)
    public Response newPages(String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        List<Upload> pages = newUploadsCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build());
        return Response.ok(pages).build();
    }

    @GET
    @Path("usages/{filename}")
    public Response usages(@PathParam("filename") String filename) {
        return Response.ok(findUsagesCommandHandler.handler(filename)).build();
    }
}
