package net.krusher.datalinks.domain.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
public class Page {

    @JsonIgnore
    private UUID id;
    private String slug;
    private String title;
    private String content;
    private String summary;
    private Set<Category> categories;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    private User creator;
}
