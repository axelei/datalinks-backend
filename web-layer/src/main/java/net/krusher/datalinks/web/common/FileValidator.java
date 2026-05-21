package net.krusher.datalinks.web.common;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;

public class FileValidator {

    private static final Set<String> DANGEROUS_TAGS = Set.of(
            "script", "object", "embed", "iframe", "form", "input", "meta", "link", "base",
            "foreignobject", "animate", "animateTransform", "set"
    );

    private static final Set<String> ALLOWED_ATTRIBUTES = Set.of(
            "id", "class", "style", "x", "y", "width", "height", "r", "cx", "cy",
            "rx", "ry", "x1", "y1", "x2", "y2", "points", "d", "fill", "stroke",
            "stroke-width", "stroke-dasharray", "stroke-dashoffset", "opacity",
            "transform", "viewbox", "xmlns", "href", "xlink:href", "offset",
            "stop-color", "stop-opacity", "gradientunits", "gradienttransform",
            "type", "result", "in", "in2", "stddeviation", "dx", "dy", "flood-color",
            "flood-opacity", "color-interpolation-filters", "orient", "refx", "refy",
            "markerwidth", "markerheight", "markerunits", "preserveaspectratio",
            "clip-path", "mask-type", "font-family", "font-size", "text-anchor",
            "dominant-baseline", "alignment-baseline", "textlength", "lengthadjust",
            "startoffset", "method", "spacing", "version", "xml:space"
    );

    public static ValidationResult validateImage(Path file, String extension) throws IOException {
        String ext = extension.toLowerCase();

        byte[] magicBytes = readMagicBytes(file, 12);

        switch (ext) {
            case "jpg", "jpeg", "jfif" -> {
                if (!matchesMagic(magicBytes, new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF})) {
                    return ValidationResult.invalid("File content does not match JPEG format");
                }
            }
            case "png" -> {
                if (!matchesMagic(magicBytes, new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A})) {
                    return ValidationResult.invalid("File content does not match PNG format");
                }
            }
            case "gif" -> {
                byte[] gifHeader = Arrays.copyOf(magicBytes, Math.min(6, magicBytes.length));
                if (!Arrays.equals(gifHeader, "GIF87a".getBytes()) &&
                        !Arrays.equals(gifHeader, "GIF89a".getBytes())) {
                    return ValidationResult.invalid("File content does not match GIF format");
                }
            }
            case "bmp" -> {
                if (!matchesMagic(magicBytes, new byte[]{0x42, 0x4D})) {
                    return ValidationResult.invalid("File content does not match BMP format");
                }
            }
            case "webp" -> {
                if (magicBytes.length < 12 ||
                        !Arrays.equals(Arrays.copyOfRange(magicBytes, 0, 4), "RIFF".getBytes()) ||
                        !Arrays.equals(Arrays.copyOfRange(magicBytes, 8, 12), "WEBP".getBytes())) {
                    return ValidationResult.invalid("File content does not match WEBP format");
                }
            }
            case "tiff" -> {
                if (!matchesMagic(magicBytes, new byte[]{0x49, 0x49, 0x2A, 0x00}) &&
                        !matchesMagic(magicBytes, new byte[]{0x4D, 0x4D, 0x00, 0x2A})) {
                    return ValidationResult.invalid("File content does not match TIFF format");
                }
            }
            case "svg" -> {
                return validateAndSanitizeSvg(file);
            }
            default -> {
                return ValidationResult.invalid("Unsupported file type: " + ext);
            }
        }

        return ValidationResult.valid(Files.newInputStream(file));
    }

    private static ValidationResult validateAndSanitizeSvg(Path file) throws IOException {
        String content = Files.readString(file);

        if (!content.toLowerCase().contains("<svg")) {
            return ValidationResult.invalid("File does not contain valid SVG content");
        }

        Document doc = Jsoup.parse(content, "", Parser.xmlParser());

        doc.select("*").forEach(el -> {
            String tagName = el.tagName().toLowerCase();

            if (DANGEROUS_TAGS.contains(tagName)) {
                el.remove();
                return;
            }

            el.attributes().asList().removeIf(attr -> {
                String key = attr.getKey().toLowerCase();
                return key.startsWith("on") || !ALLOWED_ATTRIBUTES.contains(key);
            });
        });

        Element svgElement = doc.selectFirst("svg");
        if (svgElement == null) {
            return ValidationResult.invalid("SVG file contains no valid svg element after sanitization");
        }

        String sanitized = svgElement.outerHtml();
        if (sanitized.isBlank()) {
            return ValidationResult.invalid("SVG file contains no valid content after sanitization");
        }

        String xmlDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        return ValidationResult.valid(new ByteArrayInputStream((xmlDeclaration + sanitized).getBytes()));
    }

    private static byte[] readMagicBytes(Path file, int count) throws IOException {
        try (InputStream is = Files.newInputStream(file)) {
            return is.readNBytes(count);
        }
    }

    private static boolean matchesMagic(byte[] actual, byte[] expected) {
        if (actual.length < expected.length) {
            return false;
        }
        for (int i = 0; i < expected.length; i++) {
            if (actual[i] != expected[i]) {
                return false;
            }
        }
        return true;
    }

    public record ValidationResult(boolean valid, InputStream inputStream, String errorMessage) {
        public static ValidationResult valid(InputStream is) {
            return new ValidationResult(true, is, null);
        }

        public static ValidationResult invalid(String message) {
            return new ValidationResult(false, null, message);
        }
    }
}
