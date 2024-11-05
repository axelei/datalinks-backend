package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import net.krusher.datalinks.common.CaptchaHelper;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.handler.user.ActivateUserCommandHandler;
import net.krusher.datalinks.handler.user.GetUserByLoginTokenCommand;
import net.krusher.datalinks.handler.user.GetUserByLoginTokenCommandHandler;
import net.krusher.datalinks.handler.user.RequestResetUserCommand;
import net.krusher.datalinks.handler.user.RequestResetUserCommandHandler;
import net.krusher.datalinks.handler.user.ResetUserCommandHandler;
import net.krusher.datalinks.mapper.SignupCommandMapper;
import net.krusher.datalinks.model.LoginModel;
import net.krusher.datalinks.model.SignupModel;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.handler.user.GetUserCommand;
import net.krusher.datalinks.handler.user.GetUserCommandHandler;
import net.krusher.datalinks.handler.user.LoginCommand;
import net.krusher.datalinks.handler.user.LoginCommandHandler;
import net.krusher.datalinks.handler.user.SignupCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@Log
@RestController
@RequestMapping("/user")
public class UserController {

    private final GetUserCommandHandler getUserCommandHandler;
    private final GetUserByLoginTokenCommandHandler getUserByLoginTokenCommandHandler;
    private final LoginCommandHandler loginCommandHandler;
    private final SignupCommandHandler signupCommandHandler;
    private final SignupCommandMapper signupCommandMapper;
    private final ObjectMapper objectMapper;
    private final CaptchaHelper captchaHelper;
    private final ActivateUserCommandHandler activateUserCommandHandler;
    private final ResetUserCommandHandler resetUserCommandHandler;
    private final RequestResetUserCommandHandler requestResetUserCommandHandler;

    @Autowired
    public UserController(GetUserCommandHandler getUserCommandHandler,
                          LoginCommandHandler loginCommandHandler,
                          SignupCommandHandler signupCommandHandler,
                          ObjectMapper objectMapper,
                          SignupCommandMapper signupCommandMapper,
                          GetUserByLoginTokenCommandHandler getUserByLoginTokenCommandHandler,
                          CaptchaHelper captchaHelper,
                          ActivateUserCommandHandler activateUserCommandHandler,
                          ResetUserCommandHandler resetUserCommandHandler,
                          RequestResetUserCommandHandler requestResetUserCommandHandler) {
        this.getUserCommandHandler = getUserCommandHandler;
        this.loginCommandHandler = loginCommandHandler;
        this.signupCommandHandler = signupCommandHandler;
        this.objectMapper = objectMapper;
        this.signupCommandMapper = signupCommandMapper;
        this.getUserByLoginTokenCommandHandler = getUserByLoginTokenCommandHandler;
        this.activateUserCommandHandler = activateUserCommandHandler;
        this.captchaHelper = captchaHelper;
        this.resetUserCommandHandler = resetUserCommandHandler;
        this.requestResetUserCommandHandler = requestResetUserCommandHandler;
    }

    @GetMapping("{name}/get")
    ResponseEntity<User> get(@PathVariable("name") String name, @RequestHeader(value = "login-token", required = false) String userToken) {
        return getUserCommandHandler.handler(GetUserCommand.builder().username(name).loginToken(userToken).build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{loginToken}/byLoginToken")
    ResponseEntity<User> getByLoginToken(@PathVariable("loginToken") String loginToken) {
        return getUserByLoginTokenCommandHandler.handler(GetUserByLoginTokenCommand.builder().loginToken(loginToken).build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{token}/activate")
    ResponseEntity<String> activate(@PathVariable("token") String token) {
        return Try.run(() -> activateUserCommandHandler.handler(UUID.fromString(token)))
                .map(e -> ResponseEntity.ok("OK"))
                .recover(EngineException.class, e -> ResponseEntity.badRequest().body(e.getErrorType().name()))
                .get();
    }

    @GetMapping("{token}/reset")
    ResponseEntity<String> reset(@PathVariable("token") String token) {
        return Try.run(() -> resetUserCommandHandler.handler(UUID.fromString(token)))
                .map(e -> ResponseEntity.ok("OK"))
                .recover(EngineException.class, e -> ResponseEntity.badRequest().body(e.getErrorType().name()))
                .get();
    }

    @PostMapping("{username}/requestReset")
    ResponseEntity<String> requestReset(@PathVariable("username") String username, @RequestBody String email) {
        return Try.run(() -> requestResetUserCommandHandler.handler(RequestResetUserCommand.builder().username(username).email(email).build()))
                .map(e -> ResponseEntity.ok("OK"))
                .recover(EngineException.class, e -> ResponseEntity.badRequest().body(e.getErrorType().name()))
                .get();
    }

    @PostMapping("/login")
    ResponseEntity<UUID> get(@RequestBody String body) throws JsonProcessingException {
        LoginModel loginModel = objectMapper.readValue(body, LoginModel.class);
        Optional<LoginToken> loginToken = loginCommandHandler.handler(LoginCommand.builder()
                        .username(loginModel.getUsername())
                        .password(loginModel.getPassword())
                        .build());
        return loginToken.map(token -> ResponseEntity.ok(token.getLoginToken()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody String body, HttpServletRequest request) throws JsonProcessingException {
        SignupModel signupModel = objectMapper.readValue(body, SignupModel.class);
        if (!captchaHelper.checkCaptcha(signupModel.getCaptcha(), request.getRemoteAddr())) {
            return ResponseEntity.badRequest().body("Captcha is invalid");
        }
        return Try.run(() -> signupCommandHandler.handle(signupCommandMapper.toCommand(signupModel)))
                .map(e -> ResponseEntity.ok("OK"))
                .recover(EngineException.class, e -> ResponseEntity.badRequest().body(e.getErrorType().name()))
                .get();
    }
}
