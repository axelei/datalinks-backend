package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.ResetTokenMapper;
import net.krusher.datalinks.domain.model.user.ResetToken;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ResetTokenService {

    private final EntityManager entityManager;
    private final ResetTokenMapper resetTokenMapper;
    private final ResetTokenRepositoryBean resetTokenRepositoryBean;

    @Inject
    public ResetTokenService(EntityManager entityManager, ResetTokenMapper resetTokenMapper, ResetTokenRepositoryBean resetTokenRepositoryBean) {
        this.entityManager = entityManager;
        this.resetTokenMapper = resetTokenMapper;
        this.resetTokenRepositoryBean = resetTokenRepositoryBean;
    }

    public ResetToken saveToken(ResetToken resetToken) {
        return resetTokenMapper.toModel(entityManager.merge(resetTokenMapper.toEntity(resetToken)));
    }

    public void deleteTokenById(UUID token) {
        resetTokenRepositoryBean.deleteById(token);
    }

    public Optional<ResetToken> getById(UUID resetToken) {
        return resetTokenRepositoryBean.findByIdOptional(resetToken).map(resetTokenMapper::toModel);
    }

    public Optional<ResetToken> getByUserId(UUID userId) {
        return resetTokenRepositoryBean.find("userId", userId)
                .firstResultOptional()
                .map(resetTokenMapper::toModel);
    }

    public void deleteExpired() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<ResetTokenEntity> delete = cb.createCriteriaDelete(ResetTokenEntity.class);
        Root<ResetTokenEntity> e = delete.from(ResetTokenEntity.class);
        delete.where(cb.lessThan(e.get("creationDate"), Instant.now().minus(30, ChronoUnit.DAYS)));
        entityManager.createQuery(delete).executeUpdate();
    }


}
