package net.krusher.datalinks.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import net.krusher.datalinks.handler.upload.GetFileCommand;
import net.krusher.datalinks.handler.upload.GetFileCommandHandler;
import net.krusher.datalinks.handler.upload.UploadCommand;
import net.krusher.datalinks.handler.upload.UploadCommandHandler;
import net.krusher.datalinks.model.UploadResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@RestController
@RequestMapping("/file")
public class UploadController {

    private final ObjectMapper objectMapper;
    private final UploadCommandHandler uploadCommandHandler;
    private final GetFileCommandHandler getFileCommandHandler;

    @Autowired
    public UploadController(ObjectMapper objectMapper, UploadCommandHandler uploadCommandHandler, GetFileCommandHandler getFileCommandHandler) {
        this.uploadCommandHandler = uploadCommandHandler;
        this.objectMapper = objectMapper;
        this.getFileCommandHandler = getFileCommandHandler;
    }

    @GetMapping("/get/{filename}")
    @ResponseBody
    public ResponseEntity<InputStreamResource> get(@PathVariable("filename") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        return Try.of(() -> getFileCommandHandler.handler(GetFileCommand.builder()
                .filename(title)
                .loginTokenId(toLoginToken(userToken))
                .build()))
                .map(upload -> ResponseEntity.ok()
                        .body(new InputStreamResource(upload.getInputStream())))
                .getOrElseGet(throwable -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/delete/{filename}")
    void delete(@PathVariable("filename") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {

    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> put(@RequestHeader(value = AUTH_HEADER, required = false) String userToken, @RequestParam("upload") MultipartFile upload) throws IOException {
       String url = uploadCommandHandler.handler(UploadCommand.builder()
                .inputStream(upload.getInputStream())
                .loginTokenId(toLoginToken(userToken))
                .filename(upload.getOriginalFilename())
                .build());
       return ResponseEntity.ok(UploadResponse.builder().url(url).build());
    }
}
