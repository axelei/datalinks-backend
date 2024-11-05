package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.ResetTokenMapper;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.model.user.ResetToken;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

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

    public void saveToken(ResetToken resetToken) {
        entityManager.merge(resetTokenMapper.toEntity(resetToken));
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


}
