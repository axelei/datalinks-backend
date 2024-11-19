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
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "CATEGORIES")
@Indexed
public class CategoryEntity {

    @Id
    @Column(nullable = false)
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String name;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.creationDate = Instant.now();

    }
}
