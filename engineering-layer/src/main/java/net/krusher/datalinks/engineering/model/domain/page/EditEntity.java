package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "EDITS", indexes = {
        @Index(name = "IDX_EDIT_DATE", columnList = "date"),
        @Index(name = "IDX_EDIT_USER_ID", columnList = "userId")
})
public class EditEntity {

    @Id
    @Column(nullable = false)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "page_id")
    private PageEntity page;
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
    private String ip;
    private Instant date;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.date = Instant.now();
    }
}
