package net.krusher.datalinks.domain.model.page;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;

import java.time.Instant;

@Builder
@Data
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
public class PageShort {

    private String slug;
    private String title;
    private String summary;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    private User creator;
}
