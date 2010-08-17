package controllers;

import java.util.List;

import models.User;
import play.mvc.With;

@With(Secure.class)
@Check(User.ROLE_ADMIN)
public class Admin extends Application {
	public static void index() {
		List<User> admins = User.getAdmins();
		render(admins);
	}

	public static void addAdmin(String userName) {
		User user = User.find("userName", userName).first();
		if (null != user) {
			user.role = User.ROLE_ADMIN;
			user.save();
		}
		index();
	}

	public static void deleteAdmin(String userName) {
		User user = User.find("userName", userName).first();
		if (null != user) {
			user.role = User.ROLE_USER;
			user.save();
		}
		index();
	}
}
