package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.ResetTokenMapper;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.model.user.ResetToken;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ResetTokenService {

    private final EntityManager entityManager;
    private final ResetTokenMapper resetTokenMapper;
    private final ResetTokenRepositoryBean resetTokenRepositoryBean;

    public ResetTokenService(EntityManager entityManager, ResetTokenMapper ResettokenMapper, ResetTokenRepositoryBean resetTokenRepositoryBean) {
        this.entityManager = entityManager;
        this.resetTokenMapper = ResettokenMapper;
        this.resetTokenRepositoryBean = resetTokenRepositoryBean;
    }

    public ResetToken saveToken(ResetToken resetToken) {
        return resetTokenMapper.toModel(entityManager.merge(resetTokenMapper.toEntity(resetToken)));
    }

    public void deleteTokenById(UUID token) {
        resetTokenRepositoryBean.deleteById(token);
    }

    public Optional<ResetToken> getById(UUID resetToken) {
        return resetTokenRepositoryBean.findById(resetToken).map(resetTokenMapper::toModel);
    }

    public Optional<ResetToken> getByUserId(UUID userId) {
        Example<ResetTokenEntity> example = Example.of(ResetTokenEntity.builder().userId(userId).build());
        List<ResetTokenEntity> result = resetTokenRepositoryBean.findAll(example);
        return result.stream().findFirst().map(resetTokenMapper::toModel);
    }

    public void deleteExpired() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<ResetTokenEntity> delete = cb. createCriteriaDelete(ResetTokenEntity.class);
        Root<ResetTokenEntity> e = delete.from(ResetTokenEntity.class);
        delete.where(cb.lessThan(e.get("creationDate"), Instant.now().minus(30, ChronoUnit.DAYS)));
        entityManager.createQuery(delete).executeUpdate();
    }


}
