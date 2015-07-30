package service;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import models.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import play.Play;

public final class LdapService {
	private static final Logger LOG = LoggerFactory.getLogger(LdapService.class);

	private static String getUserNameSuffix() {
		String userName = Play.configuration.getProperty("ldap.username");
		int index = userName.indexOf('@');
		if (index > 0) {
			return userName.substring(index);
		} else {
			return "";
		}
	}

	protected static LdapContext createContext(String userName, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, Play.configuration.getProperty("ldap.url"));
		env.put(Context.SECURITY_PRINCIPAL, userName);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialLdapContext(env, null);
	}

	public static boolean authenticate(String userName, String password) {
		userName = userName.trim();
		password = password.trim();

		boolean authenticated = false;

		try {
			LdapContext ctx = createContext(userName + getUserNameSuffix(), password);
			ctx.close();
			authenticated = true;
		} catch (AuthenticationException e) {
		} catch (NamingException e) {
			LOG.error("", e);
		}

		return authenticated && password.length() > 0;
	}

	public static User findUser(String userName) {
		userName = userName.trim();
		User user = null;

		try {
			LdapContext ctx = createContext(Play.configuration.getProperty("ldap.username"),
											Play.configuration.getProperty("ldap.password"));
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = String.format("(&(objectCategory=Person)(objectClass=user)(sAMAccountName=%s))", userName);
			NamingEnumeration<SearchResult> userEnum = ctx.search("", filter, ctls);
			if (userEnum.hasMoreElements()) {
				SearchResult userResult = userEnum.nextElement();
				user = new User(
					(String)userResult.getAttributes().get("sAMAccountName").get(),
					(String)userResult.getAttributes().get("name").get(),
					User.ROLE_USER
				);
				int left  = user.displayName.indexOf('(');
				int right = user.displayName.indexOf(')');
				if (left > 0 && right > 0) {
					user.displayName = user.displayName.substring(left + 1, right);
				}
			}
			ctx.close();
		} catch (NamingException e) {
			LOG.info("", e);
		}

		return user;
	}

	private static final String[] SYSTEM_ACCOUNTS = {
		"ADMINISTRATOR", "ASPNET", "CRM", "DUMMY", "GUEST", "HIKEADMIN", "IUSR", "IWAM", "KRBTGT", "MOSS", "SUPPORT", "SYSTEM", "WMANTEST", "TSINTERNETUSER", "JH"};

	private static boolean isSystemUser(User user) {
		String userName = user.userName.toUpperCase();
		String displayName = user.displayName.toUpperCase();
		for (String name : SYSTEM_ACCOUNTS) {
			if (userName.equals(name) || userName.startsWith(name) ||
				displayName.equals(name) || displayName.startsWith(name)) {
				return true;
			}
		}
		return false;
	}

	public static List<User> getAllUser() {
		List<User> users = new ArrayList<User>();

		try {
			LdapContext ctx = createContext(Play.configuration.getProperty("ldap.username"),
											Play.configuration.getProperty("ldap.password"));
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(objectCategory=Person)(objectClass=user))";
			NamingEnumeration<SearchResult> userEnum = ctx.search("", filter, ctls);
			while (userEnum.hasMoreElements()) {
				SearchResult userResult = userEnum.nextElement();
				User user = new User(
					(String)userResult.getAttributes().get("sAMAccountName").get(),
					(String)userResult.getAttributes().get("name").get(),
					User.ROLE_USER
				);
				int left  = user.displayName.indexOf('(');
				int right = user.displayName.indexOf(')');
				if (left > 0 && right > 0) {
					user.displayName = user.displayName.substring(left + 1, right);
				}
				if (!isSystemUser(user)) {
					users.add(user);
				}
			}
			ctx.close();
		} catch (NamingException e) {
			LOG.info("", e);
		}

		return users;
	}
}
