package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class BlockPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public BlockPageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

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
