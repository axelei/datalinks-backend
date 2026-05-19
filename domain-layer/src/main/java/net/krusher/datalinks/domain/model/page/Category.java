package net.krusher.datalinks.domain.model.page;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@RegisterForReflection
@NoArgsConstructor
public class Category {

    private UUID id;
    private String name;
    private String slug;
    private Instant creationDate;

}
