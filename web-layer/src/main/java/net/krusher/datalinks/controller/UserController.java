package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import net.krusher.datalinks.model.LoginModel;
import net.krusher.datalinks.model.SignupModel;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.user.GetUserCommand;
import net.krusher.datalinks.user.GetUserCommandHandler;
import net.krusher.datalinks.user.LoginCommand;
import net.krusher.datalinks.user.LoginCommandHandler;
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

@RestController
@RequestMapping("/user")
public class UserController {

    private final GetUserCommandHandler getUserCommandHandler;
    private final LoginCommandHandler loginCommandHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(GetUserCommandHandler getUserCommandHandler,
                          LoginCommandHandler loginCommandHandler,
                          ObjectMapper objectMapper) {
        this.getUserCommandHandler = getUserCommandHandler;
        this.loginCommandHandler = loginCommandHandler;
        this.objectMapper = objectMapper;
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
    ResponseEntity<String> get(@RequestBody String body) throws JsonProcessingException {
        LoginModel loginModel = objectMapper.readValue(body, LoginModel.class);
        Optional<LoginToken> loginToken = loginCommandHandler.handler(LoginCommand.builder()
                        .username(loginModel.getUsername())
                        .password(loginModel.getPassword())
                        .build());
        return loginToken.map(token -> ResponseEntity.ok(token.getToken().toString()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/signup")
    ResponseEntity<String> signup(@RequestBody String body) throws JsonProcessingException {
        SignupModel signupModel = objectMapper.readValue(body, SignupModel.class);
        return ResponseEntity.ok("Signed up");
    }
}
