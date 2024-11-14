package net.krusher.datalinks.handler.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;

import java.util.Set;

@Data
@Builder
public class PaginationCommand {
    private int page;
    private int pageSize;

    private static final Set<Integer> PAGE_SIZES = Set.of(10, 20, 50, 100);

    public void validate() {
        if (page < 0) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page number must be positive");
        }
        if (!PAGE_SIZES.contains(pageSize)) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page size must be one of " + PAGE_SIZES);
        }
    }
}
