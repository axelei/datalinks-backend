package net.krusher.datalinks.domain.model.upload;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UploadTest {

    @Test
    void builderSetsFilenameAndMd5() {
        Upload u = Upload.builder()
                .filename("file.txt")
                .md5("abcd")
                .build();

        assertEquals("file.txt", u.getFilename());
        assertEquals("abcd", u.getMd5());
    }
}
