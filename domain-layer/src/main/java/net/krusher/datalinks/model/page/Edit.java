package net.krusher.datalinks.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.User;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Edit {

    private UUID id;
    private Page page;
    private String title;
    private String content;
    @JsonIgnore
    private String ip;
    private Instant date;
    private User user;
}
