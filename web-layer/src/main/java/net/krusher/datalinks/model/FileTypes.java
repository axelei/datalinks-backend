package net.krusher.datalinks.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.MediaType;

@AllArgsConstructor
@Getter
public enum FileTypes {
    JPG(MediaType.IMAGE_JPEG),
    JPEG(MediaType.IMAGE_JPEG),
    PNG(MediaType.IMAGE_PNG),
    BMP(MediaType.valueOf("image/bmp")),
    GIF(MediaType.IMAGE_GIF),
    WEBP(MediaType.valueOf("image/webp")),
    TIFF(MediaType.valueOf("image/tiff"))
    ;

    private final MediaType mediaType;

}
