package net.krusher.datalinks.engineering.model.domain.configlet;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.engineering.model.domain.page.CategoryEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "CONFIGLETS")
public class ConfigletEntity {

    @Id
    @Column(nullable = false, name = "configlet_key")
    private String key;
    @Column(nullable = false, name = "configlet_value")
    private String value;

}
