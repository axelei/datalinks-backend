package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.page.LoginTokenEntity;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final EntityManager entityManager;
    private final UserRepositoryBean userRepositoryBean;
    private final UserMapper userMapper;

    @Autowired
    public UserService(EntityManager entityManager, UserRepositoryBean userRepositoryBean, UserMapper userMapper) {
        this.entityManager = entityManager;
        this.userRepositoryBean = userRepositoryBean;
        this.userMapper = userMapper;
    }

    public Optional<User> getByUsername(String username) {
        Example<UserEntity> example = Example.of(UserEntity.builder().username(username).build());
        List<UserEntity> result = userRepositoryBean.findAll(example);
        return result.stream().findFirst().map(userMapper::toModel);
    }

    public void save(User user) {
        UserEntity userEntity = userMapper.toEntity(user);
        entityManager.merge(userEntity);
    }

    public Optional<User> getById(UUID id) {
        return userRepositoryBean.findById(id).map(userMapper::toModel);
    }

    public Optional<User> getByActivationToken(UUID activationToken) {
        Example<UserEntity> example = Example.of(UserEntity.builder().activationToken(activationToken).build());
        List<UserEntity> result = userRepositoryBean.findAll(example);
        return result.stream().findFirst().map(userMapper::toModel);
    }

    public Optional<User> getByResetToken(UUID resetToken) {
        Example<UserEntity> example = Example.of(UserEntity.builder().resetToken(resetToken).build());
        List<UserEntity> result = userRepositoryBean.findAll(example);
        return result.stream().findFirst().map(userMapper::toModel);
    }

}
