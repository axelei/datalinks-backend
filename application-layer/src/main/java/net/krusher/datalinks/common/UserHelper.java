package net.krusher.datalinks.common;

import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserHelper {

    public boolean userCanEdit(Page page, User user) {
        UserLevel neededLevel = Optional.ofNullable(page.getEditBlock()).orElse(UserLevel.GUEST);
        UserLevel userLevel = Optional.ofNullable(user.getLevel()).orElse(UserLevel.GUEST);
        Optional.ofNullable(user).map(User::getLevel);
        return userLevel.getLevel() >= neededLevel.getLevel();
    }

    public boolean userCanCreate(Page page, User user) {
        UserLevel neededLevel = UserLevel.GUEST;
        UserLevel userLevel = Optional.ofNullable(user.getLevel()).orElse(UserLevel.GUEST);
        return userLevel.getLevel() >= neededLevel.getLevel();
    }

}
