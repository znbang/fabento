package controllers;

import models.User;
import play.cache.Cache;
import play.mvc.Before;
import service.UserService;

public class Controller extends play.mvc.Controller {
    static final UserService userService = new UserService();

    @Before
    static void initCurrentUser() {
        renderArgs.put("currentUser", getCurrentUser());
    }

    static User getCurrentUser() {
        User user = Cache.get(session.getId(), User.class);
        if (user != null) {
            return user;
        }

        if (session.contains("userId")) {
            user = User.findById(Long.parseLong(session.get("userId")));
            if (user != null) {
                Cache.set(session.getId(), user);
            }
        }

        return user;
    }

    static void setCurrentUser(User user) {
        session.put("userId", user.id);
        Cache.set(session.getId(), user);
    }

    static void resetCurrentUser() {
        Cache.delete(session.getId());
        session.clear();
    }
}
