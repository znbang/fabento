package controllers;

import models.User;
import models.UserRole;
import play.mvc.With;

import java.util.List;

@With(RequireAdmin.class)
public class Admin extends Application {
	public static void index() {
		List<User> admins = User.getAdmins();
		render(admins);
	}

	public static void addAdmin(String userName) {
		User user = User.findByUserName(userName);
		if (null != user) {
			user.role = UserRole.admin;
			user.save();
		}
		index();
	}

	public static void deleteAdmin(String userName) {
		User user = User.findByUserName(userName);
		if (null != user) {
			user.role = UserRole.user;
			user.save();
		}
		index();
	}
}
