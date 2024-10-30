package net.krusher.datalinks.user;

import net.krusher.datalinks.engineering.user.UserRepository;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserCommandHandler {

    private final UserRepository userRepository;

    @Autowired
    public GetUserCommandHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> handler(GetUserCommand getUserCommand) {
        return userRepository.get(getUserCommand.getUsername());
    }
}
