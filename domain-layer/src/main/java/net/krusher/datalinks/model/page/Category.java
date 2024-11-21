package net.krusher.datalinks.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    private UUID id;
    private String name;
    private String slug;
    private Instant creationDate;

}
