package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.Embeddable;
import lombok.Data;

import java.util.UUID;

@Data
@Embeddable
public class CategoryLinkEntityKey {

    private String name;
    private UUID pageId;

}
