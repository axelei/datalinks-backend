package net.krusher.datalinks.domain.model.configlet;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@RegisterForReflection
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Configlet {
    private ConfigletKey key;
    private String value;

    public static Configlet of(ConfigletKey key, String value) {
        return Configlet.builder().key(key).value(value).build();
    }
}
