package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    private final EntityManager entityManager;
    private final UserRepositoryBean userRepositoryBean;
    private final UserMapper userMapper;
    private final SearchService searchService;

    @Inject
    public UserService(EntityManager entityManager, UserRepositoryBean userRepositoryBean, UserMapper userMapper, SearchService searchService) {
        this.entityManager = entityManager;
        this.userRepositoryBean = userRepositoryBean;
        this.userMapper = userMapper;
        this.searchService = searchService;
    }

    public Optional<User> getByUsername(String username) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);

        Root<UserEntity> user = cq.from(UserEntity.class);
        Predicate usernamePredicate = cb.equal(cb.lower(user.get("username")), username.toLowerCase());
        cq.where(usernamePredicate);

        TypedQuery<UserEntity> query = entityManager.createQuery(cq);

        return query.getResultList().stream().findFirst().map(userMapper::toModel);
    }

    public void save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        userEntity = entityManager.merge(userEntity);
        searchService.indexUser(userEntity);
    }

    public Optional<User> getById(UUID id) {
        return userRepositoryBean.findByIdOptional(id).map(userMapper::toModel);
    }

    public Optional<User> getByActivationToken(UUID activationToken) {
        return userRepositoryBean.find("activationToken", activationToken)
                .firstResultOptional()
                .map(userMapper::toModel);
    }

}
