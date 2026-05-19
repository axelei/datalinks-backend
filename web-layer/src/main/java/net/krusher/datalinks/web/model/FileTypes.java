package net.krusher.datalinks.web.model;

import jakarta.ws.rs.core.MediaType;

@AllArgsConstructor
@Getter
public enum FileTypes {
    JPG(new MediaType("image", "jpeg")),
    JPEG(new MediaType("image", "jpeg")),
    PNG(new MediaType("image", "png")),
    BMP(new MediaType("image", "bmp")),
    GIF(new MediaType("image", "gif")),
    WEBP(new MediaType("image", "webp")),
    TIFF(new MediaType("image", "tiff")),
    SVG(new MediaType("image", "svg+xml")),
    JFIF(new MediaType("image", "jpeg")),
    ;

    private final MediaType mediaType;

}
