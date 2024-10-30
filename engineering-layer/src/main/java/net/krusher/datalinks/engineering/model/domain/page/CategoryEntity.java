package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "CATEGORIES")
public class CategoryEntity {

    @Id
    @Column(nullable = false)
    private String name;
}
