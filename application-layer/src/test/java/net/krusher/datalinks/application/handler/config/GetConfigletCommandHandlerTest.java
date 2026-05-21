package net.krusher.datalinks.application.handler.config;

import net.krusher.datalinks.domain.model.configlet.Configlet;
import net.krusher.datalinks.application.handler.config.GetConfigletCommandHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

class GetConfigletCommandHandlerTest {

    @Test
    void handlerReturnsConfigletFromKey() {
        GetConfigletCommandHandler handler = new GetConfigletCommandHandler(null);
        Optional<Configlet> res = handler.handler(GetConfigletCommand.builder().key("READ_LEVEL").build());
        assertTrue(res.isPresent());
        assertEquals("READ_LEVEL", res.get().getKey().name());
        assertEquals("value", res.get().getValue());
    }
}
