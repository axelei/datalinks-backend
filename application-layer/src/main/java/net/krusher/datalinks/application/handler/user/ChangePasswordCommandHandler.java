package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.email.PasswordChangeParams;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class ChangePasswordCommandHandler {

    private final UserHelper userHelper;
    private final UserService userService;
    private final EmailService emailService;


    @Transactional
    public void handler(ChangePasswordCommand changePasswordCommand) {
        Optional<User> user = userHelper.getUserFromLoginToken(changePasswordCommand.getLoginToken());
        if (user.isEmpty()) {
            throw new EngineException(ErrorType.USER_NOT_FOUND, "User not found");
        }
        String salt = RandomStringUtils.secure().nextAlphanumeric(8);
        user.get().setSalt(salt);
        user.get().setPassword(DigestUtils.sha256Hex(salt + changePasswordCommand.getPassword()));
        userService.save(user.get());
        emailService.sendPasswordChanged(user.get().getEmail(),
                Map.of(PasswordChangeParams.NAME, user.get().useName()),
                user.get().getLanguage());
    }
}
