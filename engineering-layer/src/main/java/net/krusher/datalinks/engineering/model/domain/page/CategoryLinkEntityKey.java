package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Embeddable
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryLinkEntityKey {

    private String name;
    private UUID pageId;

}
