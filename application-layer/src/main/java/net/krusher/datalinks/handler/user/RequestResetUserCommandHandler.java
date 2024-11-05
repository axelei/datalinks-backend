package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.email.RequestResetTokenParams;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.user.ResetToken;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
public class RequestResetUserCommandHandler {

    private final UserService userService;
    private final ResetTokenService resetTokenService;
    private final EmailService emailService;

    @Autowired
    public RequestResetUserCommandHandler(UserService userService, ResetTokenService resetTokenService, EmailService emailService) {
        this.userService = userService;
        this.resetTokenService = resetTokenService;
        this.emailService = emailService;
    }

    @Transactional
    public void handler(RequestResetUserCommand requestResetuserCommand) {
        Optional<User> user = userService.getByUsername(requestResetuserCommand.getUsername());
        if (user.isEmpty() || (!user.get().getEmail().equals(requestResetuserCommand.getEmail()))) {
            throw new EngineException(ErrorType.USER_NOT_FOUND_OR_MAIL_MISMATCH, "User not found or email mismatch");
        }
        Optional<ResetToken> existingResetToken = resetTokenService.getByUserId(user.get().getId());
        if (existingResetToken.isPresent()) {
            throw new EngineException(ErrorType.RESET_REQUEST_EXISTS, "Reset request already exists");
        }

        ResetToken resetToken = ResetToken.builder().userId(user.get().getId()).build();
        resetTokenService.saveToken(resetToken);

        emailService.sendRequestResetMessage(user.get().getEmail(),
                Map.of(RequestResetTokenParams.NAME, user.get().getUsername(),
                        RequestResetTokenParams.RESET_TOKEN, resetToken.getResetToken().toString()),
                user.get().getLanguage());

    }
}
