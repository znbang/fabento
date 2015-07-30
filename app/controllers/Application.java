package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;

public class Application extends Controller {
	@Before
	static void global() {
	}

	static User getCurrentUser() {
		User user = User.findByUserName(session.get("username").toLowerCase());
		if (null == user) {
			try {
				Secure.login();
			} catch (Throwable ignored) {
			}
		}
		return user;
	}
}