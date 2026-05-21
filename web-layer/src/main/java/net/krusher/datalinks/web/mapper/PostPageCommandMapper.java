package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.page.PostPageCommand;
import net.krusher.datalinks.web.model.PostPageRequestModel;

@Mapper(componentModel = "jakarta-cdi")
public interface PostPageCommandMapper {
    PostPageCommand toCommand(PostPageRequestModel model);
}
