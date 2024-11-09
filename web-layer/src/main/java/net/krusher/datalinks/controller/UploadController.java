package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.upload.GetFileCommand;
import net.krusher.datalinks.handler.upload.GetFileCommandHandler;
import net.krusher.datalinks.handler.upload.NewUploadsCommandHandler;
import net.krusher.datalinks.handler.upload.UploadCommand;
import net.krusher.datalinks.handler.upload.UploadCommandHandler;
import net.krusher.datalinks.model.FileTypes;
import net.krusher.datalinks.model.PaginationModel;
import net.krusher.datalinks.model.UploadResponse;
import net.krusher.datalinks.model.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
    private final GetFileCommandHandler getFileCommandHandler;
    private final NewUploadsCommandHandler newUploadsCommandHandler;

    @Autowired
    public UploadController(ObjectMapper objectMapper,
                            UploadCommandHandler uploadCommandHandler,
                            GetFileCommandHandler getFileCommandHandler,
                            NewUploadsCommandHandler newUploadsCommandHandler) {
        this.uploadCommandHandler = uploadCommandHandler;
        this.objectMapper = objectMapper;
        this.getFileCommandHandler = getFileCommandHandler;
        this.newUploadsCommandHandler = newUploadsCommandHandler;
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
    void delete(@PathVariable("filename") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {

    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> put(@RequestHeader(value = AUTH_HEADER, required = false) String userToken, @RequestParam("upload") MultipartFile upload) throws IOException {
       String extension = upload.getOriginalFilename().substring(upload.getOriginalFilename().lastIndexOf('.') + 1);
       if (Arrays.stream(FileTypes.values()).noneMatch(fileType -> fileType.name().equalsIgnoreCase(extension))) {
           return ResponseEntity.badRequest().build();
       }
       String url = uploadCommandHandler.handler(UploadCommand.builder()
                .inputStream(upload.getInputStream())
                .loginTokenId(toLoginToken(userToken))
                .filename(upload.getOriginalFilename())
                .build());
       return ResponseEntity.ok(UploadResponse.builder().url(url).build());
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
}
