package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {
	@Before
	static void global() {
	}

	static User getCurrentUser() {
		User user = User.find("userName", session.get("username").toLowerCase()).first();
		if (null == user) {
			try {
				Secure.login();
			} catch (Throwable e) {
			}
		}
		return user;
	}
}