package net.krusher.datalinks.model.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    private UUID id;
    private String name;
    private String slug;
    private Instant creationDate;

}
