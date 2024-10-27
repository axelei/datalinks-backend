package net.krusher.datalinks.page;

import net.krusher.datalinks.model.page.Page;

public class GetPageCommandHandler {

    public Page handler(String title) {
        return Page.builder()
            .title(title)
            .content("content")
            .build();
    }
}
