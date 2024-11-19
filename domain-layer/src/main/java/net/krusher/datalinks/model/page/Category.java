package net.krusher.datalinks.model.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category implements Foundable {
    private String name;
    private Instant creationDate;

    @Override
    public Foundling toFoundling() {
        return Foundling.builder()
                .id(UUID.randomUUID())
                .title(name)
                .build();
    }
}
