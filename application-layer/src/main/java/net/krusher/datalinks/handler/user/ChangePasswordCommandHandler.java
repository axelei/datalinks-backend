package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.email.PasswordChangeParams;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class ChangePasswordCommandHandler {

    private final UserHelper userHelper;
    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public ChangePasswordCommandHandler(UserHelper userHelper, UserService userService, EmailService emailService) {
        this.userHelper = userHelper;
        this.userService = userService;
        this.emailService = emailService;
    }

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
