package controllers;

import models.User;

public class Authenticate extends Controller {
    public static void login() {
        render();
    }

    public static void auth(String userName, String password) {
        checkAuthenticity();

        User user = userService.authenticate(userName, password);
        if (user != null) {
            setCurrentUser(user);
            UserOrder.index();
        } else {
            params.flash();
            flash.error("secure.error");
            login();
        }
    }

    public static void logout() {
        resetCurrentUser();
        flash.success("secure.logout");
        login();
    }
}
