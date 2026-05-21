package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.email.ResetParams;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.user.ResetToken;
import net.krusher.datalinks.domain.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class ResetPasswordCommandHandler {

    private final UserService userService;
    private final ResetTokenService resetTokenService;
    private final EmailService emailService;


    @Transactional
    public void handler(UUID resetTokenId) {
        Optional<ResetToken> resetToken = resetTokenService.getById(resetTokenId);
        if (resetToken.isEmpty()) {
            throw new EngineException(ErrorType.RESET_REQUEST_NOT_FOUND, "User reset token not found");
        }
        User user = userService.getById(resetToken.get().getUserId()).orElseThrow();
        String newPassword = RandomStringUtils.secureStrong().nextAlphanumeric(8);
        String salt = RandomStringUtils.secure().nextAlphanumeric(8);
        user.setSalt(salt);
        user.setPassword(DigestUtils.sha256Hex(salt + newPassword));

        userService.save(user);
        resetTokenService.deleteTokenById(resetTokenId);

        emailService.sendResetMessage(user.getEmail(), Map.of(
                ResetParams.NAME, user.useName(),
                ResetParams.NEW_PASSWORD, newPassword),
                user.getLanguage());

    }
}
