package net.krusher.datalinks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReCaptchaModel {
    private String secret;
    private String response;
    private String remoteIp;
}
