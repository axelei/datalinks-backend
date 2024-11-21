package net.krusher.datalinks.engineering.model.domain.page;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "CATEGORIES_PAGES", indexes = {
        @Index(name = "IDX_CATEGORY_PAGE_CATEGORY_ID", columnList = "categoryId"),
        @Index(name = "IDX_CATEGORY_PAGE_PAGE_ID", columnList = "pageId")
})
public class CategoryLinkEntity {

    @Id
    @Column(nullable = false)
    private CategoryLinkEntityKey id;

}
