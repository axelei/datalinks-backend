package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
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
import net.krusher.datalinks.model.page.PageShort;
import net.krusher.datalinks.model.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@RestController
@RequestMapping("/file")
public class UploadController {

    private final ObjectMapper objectMapper;
    private final UploadCommandHandler uploadCommandHandler;
    private final UpdateUploadCommandHandler updateUploadCommandHandler;
    private final GetFileCommandHandler getFileCommandHandler;
    private final NewUploadsCommandHandler newUploadsCommandHandler;
    private final FindUsagesCommandHandler findUsagesCommandHandler;
    private final DeleteUploadCommandHandler deleteUploadCommandHandler;

    @Autowired
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

    @GetMapping("/lookAt/{filename}")
    @ResponseBody
    public ResponseEntity<Upload> lookAt(@PathVariable("filename") String filename, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        Upload upload = getFileCommandHandler.handler(GetFileCommand.builder()
                .filename(filename)
                .loginTokenId(toLoginToken(userToken))
                .build());
        upload.setInputStream(null);
        return ResponseEntity.ok(upload);
    }

    @GetMapping("/get/{filename}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> get(@PathVariable("filename") String filename, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        return Try.of(() -> getFileCommandHandler.handler(GetFileCommand.builder()
                .filename(filename)
                .loginTokenId(toLoginToken(userToken))
                .build()))
                .map(upload -> ResponseEntity.ok()
                        .contentType(getMediaType(upload.getFilename()))
                        .body(new InputStreamResource(upload.getInputStream())))
                .getOrElseGet(throwable -> ResponseEntity.notFound().build());
    }

    private MediaType getMediaType(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        return FileTypes.valueOf(extension.toUpperCase()).getMediaType();
    }

    @DeleteMapping("/delete/{filename}")
    ResponseEntity<String> delete(@PathVariable("filename") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        deleteUploadCommandHandler.handler(DeleteUploadCommand.builder()
                .filename(title)
                .loginToken(toLoginToken(userToken))
                .build());
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> put(@RequestHeader(value = AUTH_HEADER, required = false) String userToken, @RequestParam("upload") MultipartFile upload, HttpServletRequest request) throws IOException {
       String extension = upload.getOriginalFilename().substring(upload.getOriginalFilename().lastIndexOf('.') + 1);
       if (Arrays.stream(FileTypes.values()).noneMatch(fileType -> fileType.name().equalsIgnoreCase(extension))) {
           return ResponseEntity.badRequest().build();
       }
       String url = uploadCommandHandler.handler(UploadCommand.builder()
                .inputStream(upload.getInputStream())
                .loginTokenId(toLoginToken(userToken))
                .filename(upload.getOriginalFilename())
                .ip(request.getRemoteAddr())
                .build());
       return ResponseEntity.ok(UploadResponse.builder().url(url).build());
    }

    @PutMapping("/update")
    public ResponseEntity<String> update(@RequestHeader(value = AUTH_HEADER, required = false) String userToken, @RequestBody String body, HttpServletRequest request) throws JsonProcessingException {
        UpdateUploadModel paginationModel = objectMapper.readValue(body, UpdateUploadModel.class);
        updateUploadCommandHandler.handler(UpdateUploadCommand.builder()
                .loginToken(toLoginToken(userToken))
                .description(paginationModel.getDescription())
                .filename(paginationModel.getFilename())
                .ip(request.getRemoteAddr())
                .build());
        return ResponseEntity.ok("ok");
    }

    @PostMapping("newUploads")
    public ResponseEntity<List<Upload>> newPages(@RequestBody String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        List<Upload> pages = newUploadsCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build());
        return ResponseEntity.ok(pages);
    }

    @GetMapping("-usages/{filename}")
    public ResponseEntity<List<PageShort>> usages(@PathVariable("filename") String filename) {
        return ResponseEntity.ok(findUsagesCommandHandler.handler(filename));
    }
}
