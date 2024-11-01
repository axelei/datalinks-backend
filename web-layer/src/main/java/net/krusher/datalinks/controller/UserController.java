package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.extern.java.Log;
import net.krusher.datalinks.exception.EngineException;
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
    private final LoginCommandHandler loginCommandHandler;
    private final SignupCommandHandler signupCommandHandler;
    private final SignupCommandMapper signupCommandMapper;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(GetUserCommandHandler getUserCommandHandler,
                          LoginCommandHandler loginCommandHandler,
                          SignupCommandHandler signupCommandHandler,
                          ObjectMapper objectMapper,
                          SignupCommandMapper signupCommandMapper) {
        this.getUserCommandHandler = getUserCommandHandler;
        this.loginCommandHandler = loginCommandHandler;
        this.signupCommandHandler = signupCommandHandler;
        this.objectMapper = objectMapper;
        this.signupCommandMapper = signupCommandMapper;
    }

    @GetMapping("{name}/get")
    ResponseEntity<User> get(@PathVariable("name") String name, @RequestHeader(value = "user-token", required = false) String userToken) {
        return getUserCommandHandler.handler(GetUserCommand.builder().username(name).userToken(userToken).build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("{token}/activate")
    ResponseEntity<String> activate(@PathVariable("token") String token) {
        return ResponseEntity.ok("Activated");
    }

    @PostMapping("/login")
    ResponseEntity<UUID> get(@RequestBody String body) throws JsonProcessingException {
        LoginModel loginModel = objectMapper.readValue(body, LoginModel.class);
        Optional<LoginToken> loginToken = loginCommandHandler.handler(LoginCommand.builder()
                        .username(loginModel.getUsername())
                        .password(loginModel.getPassword())
                        .build());
        return loginToken.map(token -> ResponseEntity.ok(token.getToken()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
/*
    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody String body) throws JsonProcessingException {
        SignupModel signupModel = objectMapper.readValue(body, SignupModel.class);
        Try.run(() -> signupCommandHandler.handle(signupMapper.toCommand(signupModel)))
                .onSuccess(e -> ResponseEntity.ok("User created"))
                .onFailure(e -> ResponseEntity.badRequest().body(e.getMessage()));
        return ResponseEntity.ok("User created");
    }
*/
    @PostMapping("/signup")
    ResponseEntity<String> signup2(@RequestBody String body) throws JsonProcessingException {
        SignupModel signupModel = objectMapper.readValue(body, SignupModel.class);
        return Try.run(() -> signupCommandHandler.handle(signupCommandMapper.toCommand(signupModel)))
                .map(e -> ResponseEntity.ok("User created"))
                .recover(EngineException.class, e -> ResponseEntity.badRequest().body(e.getErrorType().name()))
                .get();
    }
}
