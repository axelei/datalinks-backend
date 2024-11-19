package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "CATEGORIES")
public class CategoryEntity {

    @Id
    @Column(nullable = false)
    private String name;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.creationDate = Instant.now();

    }
}
