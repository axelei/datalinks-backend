package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.mapper.SignupMapper;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SignupCommandHandler {

    private final UserService userService;
    private final SignupMapper signupMapper;

    @Autowired
    public SignupCommandHandler(UserService userService, SignupMapper signupMapper) {
        this.userService = userService;
        this.signupMapper = signupMapper;
    }

    @Transactional
    public void handle(SignupCommand signupCommand) {
        validateSignup(signupCommand);

        User user = signupMapper.toModel(signupCommand);
        String salt = RandomStringUtils.secure().nextAlphanumeric(8);
        user.setSalt(salt);
        user.setPassword(DigestUtils.sha256Hex(salt + signupCommand.getPassword()));
        user.setLevel(UserLevel.USER);

        userService.save(user);
    }

    private void validateSignup(SignupCommand signupCommand) {
        Optional<User> user = userService.getByUsername(signupCommand.getUsername());
        if (user.isPresent()) {
            throw new EngineException(ErrorType.USER_EXISTS, "User already exists");
        }
        if (signupCommand.getPassword().length() < 8) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Password must be at least 8 characters long");
        }
        if (!StringUtils.isAlphanumeric(signupCommand.getUsername()) || signupCommand.getUsername().length() < 3 || signupCommand.getUsername().length() > 20) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Username must be alphanumeric and between 3 and 20 characters long");
        }
        if (!signupCommand.getEmail().matches("([A-Za-z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,6})")) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Invalid email address");
        }
    }

}
