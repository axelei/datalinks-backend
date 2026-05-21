package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.user.UserLevel;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class BlockPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;


    @Transactional
    public void handler(BlockPageCommand blockPageCommand) {
        if (!userHelper.isAdmin(blockPageCommand.getLoginToken())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't block pages");
        }
        if (UserLevel.valueOf(blockPageCommand.getReadBlock()).isEmpty() || UserLevel.valueOf(blockPageCommand.getWriteBlock()).isEmpty()) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Invalid block level");

        }
        String slug = SLUGIFY.slugify(blockPageCommand.getTitle());
        pageService.block(slug, UserLevel.valueOf(blockPageCommand.getReadBlock()).get(), UserLevel.valueOf(blockPageCommand.getWriteBlock()).get());

    }

}
