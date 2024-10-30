package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.user.GetUserCommand;
import net.krusher.datalinks.user.GetUserCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final GetUserCommandHandler getUserCommandHandler;

    @Autowired
    public UserController(GetUserCommandHandler getUserCommandHandler) {
        this.getUserCommandHandler = getUserCommandHandler;
    }

    @GetMapping("{name}")
    ResponseEntity<User> get(@PathVariable("name") String name, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        return getUserCommandHandler.handler(GetUserCommand.builder().username(name).userToken(userToken).build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
