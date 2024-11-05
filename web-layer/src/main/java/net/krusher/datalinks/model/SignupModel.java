package net.krusher.datalinks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupModel {
    private String username;
    private String password;
    private String email;
    private String name;
    private String captcha;
    private String language;
}
