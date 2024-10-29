package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("{name}")
    ResponseEntity<User> get(@PathVariable("name") String name, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        return ResponseEntity.ok(User.builder()
            .username(name)
            .email("email")
            .name("name")
            .build());
    }
}
