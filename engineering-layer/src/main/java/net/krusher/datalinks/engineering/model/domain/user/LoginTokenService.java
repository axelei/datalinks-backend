package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.TokenMapper;
import net.krusher.datalinks.model.user.LoginToken;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginTokenService {

    private final EntityManager entityManager;
    private final TokenMapper tokenMapper;
    private final LoginTokenRepositoryBean loginTokenRepositoryBean;

    public LoginTokenService(EntityManager entityManager, TokenMapper tokenMapper, LoginTokenRepositoryBean loginTokenRepositoryBean) {
        this.entityManager = entityManager;
        this.tokenMapper = tokenMapper;
        this.loginTokenRepositoryBean = loginTokenRepositoryBean;
    }

    public void saveToken(LoginToken loginToken) {
        entityManager.merge(tokenMapper.toEntity(loginToken));
    }

    public Optional<LoginToken> getById(UUID token) {
        if (Objects.isNull(token)) {
            return Optional.empty();
        }
        return loginTokenRepositoryBean.findById(token).map(tokenMapper::toModel);
    }


}
