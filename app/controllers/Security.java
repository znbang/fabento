package controllers;

import models.User;
import service.UserService;

public class Security extends Secure.Security {
    static boolean authenticate(String userName, String password) {
    	userName = userName.toLowerCase();
        User u = UserService.authenticate(userName, password);
        if (u != null) {
        	session.put("userName", u.userName);
        	session.put("displayName", u.displayName);
        	session.put("role", u.role);
        }
        return u != null;
    }

    static boolean check(String profile) {
   		String role = session.get("role");
   		if (null == role) {
   			User u = User.findByUserName(session.get("username").toLowerCase());
        	session.put("userName", u.userName);
        	session.put("displayName", u.displayName);
   			session.put("role", u.role);
   		}
    	if ("guest".equals(profile)) {
    		return !isConnected();
    	} else if ("user".equals(profile)) {
   			return "user".equals(role) || "admin".equals(role);
   		} else if ("admin".equals(profile)) {
   			return "admin".equals(role);
   		} else {
   			return false;
   		}
    }
}
