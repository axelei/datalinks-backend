package net.krusher.datalinks.model.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Category {
    private String name;
}
