package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.email.RequestResetTokenParams;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.user.ResetToken;
import net.krusher.datalinks.domain.model.user.User;

import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class RequestResetUserCommandHandler {

    private final UserService userService;
    private final ResetTokenService resetTokenService;
    private final EmailService emailService;


    @Transactional
    public void handler(RequestResetUserCommand requestResetuserCommand) {
        Optional<User> user = userService.getByUsername(requestResetuserCommand.getUsername());
        if (user.isEmpty() || (!user.get().getEmail().equalsIgnoreCase(requestResetuserCommand.getEmail()))) {
            throw new EngineException(ErrorType.USER_NOT_FOUND_OR_MAIL_MISMATCH, "User not found or email mismatch");
        }
        Optional<ResetToken> existingResetToken = resetTokenService.getByUserId(user.get().getId());
        if (existingResetToken.isPresent()) {
            throw new EngineException(ErrorType.RESET_REQUEST_EXISTS, "Reset request already exists");
        }

        ResetToken resetToken = ResetToken.builder().userId(user.get().getId()).build();
        resetToken = resetTokenService.saveToken(resetToken);

        emailService.sendRequestResetMessage(user.get().getEmail(),
                Map.of(RequestResetTokenParams.NAME, user.get().displayName(),
                        RequestResetTokenParams.RESET_TOKEN, resetToken.getResetToken().toString()),
                user.get().getLanguage());

    }
}
