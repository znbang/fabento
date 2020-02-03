package controllers;

import models.User;
import play.mvc.Before;

public class RequireAdmin extends Controller {
    @Before
    static void requireAdmin() {
        User user = getCurrentUser();
        if (user == null || !user.isAdmin()) {
            Authenticate.login();
        }
    }
}
