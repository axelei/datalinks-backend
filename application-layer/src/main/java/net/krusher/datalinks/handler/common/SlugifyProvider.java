package net.krusher.datalinks.handler.common;

import com.github.slugify.Slugify;

public class SlugifyProvider {

    public static final Slugify SLUGIFY = Slugify.builder().transliterator(true).build();
}
