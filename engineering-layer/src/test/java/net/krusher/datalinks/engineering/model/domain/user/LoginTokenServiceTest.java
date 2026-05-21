package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.engineering.mapper.TokenMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LoginTokenServiceTest {

    @Test
    void getByIdReturnsEmptyForNull() {
        TokenMapper mapper = Mockito.mock(TokenMapper.class);
        LoginTokenRepositoryBean repo = Mockito.mock(LoginTokenRepositoryBean.class);

        LoginTokenService svc = new LoginTokenService(Mockito.mock(EntityManager.class), mapper, repo);
        Optional<LoginToken> res = svc.getById(null);
        assertFalse(res.isPresent());
    }

    @Test
    void getByIdReturnsMappedWhenFound() {
        TokenMapper mapper = Mockito.mock(TokenMapper.class);
        LoginTokenRepositoryBean repo = Mockito.mock(LoginTokenRepositoryBean.class);

        LoginTokenEntity entity = new LoginTokenEntity();
        when(repo.findByIdOptional(any(UUID.class))).thenReturn(Optional.of(entity));

        LoginToken model = LoginToken.builder().loginToken(UUID.randomUUID()).build();
        when(mapper.toModel(entity)).thenReturn(model);

        LoginTokenService svc = new LoginTokenService(Mockito.mock(EntityManager.class), mapper, repo);
        Optional<LoginToken> res = svc.getById(UUID.randomUUID());
        assertEquals(model, res.get());
    }
}
