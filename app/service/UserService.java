package service;

import models.User;

public final class UserService {
	public User authenticate(String userName, String password) {
		User ldapUser = LdapService.authenticate(userName, password);
		if (ldapUser == null) {
			return null;
		}

		User user = User.findByUserName(userName.toLowerCase());
		if (user != null) {
			return user;
		}

		return ldapUser.save();
	}

	public void importUsers() {
		for (User user : LdapService.getUsers()) {
			if (null == User.findByUserName(user.userName)) {
				user.save();
			}
		}
	}
}
