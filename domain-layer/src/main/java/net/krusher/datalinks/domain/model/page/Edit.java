package net.krusher.datalinks.domain.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.user.User;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
public class Edit {

    private UUID id;
    private PageShort page;
    private String content;
    @JsonIgnore
    private String ip;
    private Instant date;
    private User user;
}
