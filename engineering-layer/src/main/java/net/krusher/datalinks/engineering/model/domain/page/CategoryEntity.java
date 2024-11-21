package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "CATEGORIES")
@Indexed
public class CategoryEntity implements Foundable {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String name;
    private String slug;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.creationDate = Instant.now();
        this.id = UUID.randomUUID();
    }

    @Override
    public Foundling toFoundling() {
        return Foundling.builder()
                .id(UUID.randomUUID())
                .title(this.getName())
                .type(Foundling.FoundlingType.CATEGORY)
                .build();
    }
}
