package net.krusher.datalinks.page;

import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.page.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Data
@Builder
public class GetPageCommand {
    private String title;
    private String userToken;
}
