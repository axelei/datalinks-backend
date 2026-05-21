package net.krusher.datalinks.web.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.page.Category;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class PostPageRequestModel {
    private String title;
    private String content;
    private Category[] categories;
    private UUID loginTokenId;
    private String ip;
}
