package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.TokenMapper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadUsageEntity;
import net.krusher.datalinks.model.user.LoginToken;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public void deleteExpired() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<LoginTokenEntity> delete = cb. createCriteriaDelete(LoginTokenEntity.class);
        Root<LoginTokenEntity> e = delete.from(LoginTokenEntity.class);
        delete.where(cb.lessThan(e.get("creationDate"), Instant.now().minus(30, ChronoUnit.DAYS)));
        entityManager.createQuery(delete).executeUpdate();
    }


}
