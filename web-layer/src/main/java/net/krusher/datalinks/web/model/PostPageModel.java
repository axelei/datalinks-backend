package net.krusher.datalinks.web.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.page.Category;

@Data
@Builder
@AllArgsConstructor
@RegisterForReflection
@NoArgsConstructor
public class PostPageModel {
    private String content;
    private Category[] categories;
}
