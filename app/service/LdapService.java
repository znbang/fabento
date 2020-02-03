package service;

import models.User;
import models.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Play;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public final class LdapService {
	private static final Logger LOG = LoggerFactory.getLogger(LdapService.class);

	private static String getUserNameSuffix() {
		String userName = Play.configuration.getProperty("ldap.user");
		int index = userName.indexOf('@');
		if (index > 0) {
			return userName.substring(index);
		} else {
			return "";
		}
	}

	protected static LdapContext createContext(String userName, String password) throws NamingException {
		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, Play.configuration.getProperty("ldap.url"));
		env.put(Context.SECURITY_PRINCIPAL, userName);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialLdapContext(env, null);
	}

	public static User authenticate(String userName, String password) {
		userName = userName.trim();
		password = password.trim();

		if (userName.isEmpty() || password.isEmpty()) {
			return null;
		}

		User user = null;

		try {
			LdapContext ctx = createContext(userName + getUserNameSuffix(), password);
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = String.format("(&(objectCategory=Person)(objectClass=user)(sAMAccountName=%s))", userName);
			NamingEnumeration<SearchResult> userEnum = ctx.search("", filter, ctls);
			if (userEnum.hasMoreElements()) {
				SearchResult userResult = userEnum.nextElement();
				user = new User(
						(String) userResult.getAttributes().get("sAMAccountName").get(),
						(String) userResult.getAttributes().get("name").get(),
						UserRole.user
				);
			}
			ctx.close();
		} catch (AuthenticationException e) {
			LOG.debug("User login with incorrect password: {}", userName);
		} catch (NamingException e) {
			LOG.debug("", e);
		}

		return user;
	}

	public static List<User> getUsers() {
		List<User> users = new ArrayList<>();

		try {
			LdapContext ctx = createContext(
					Play.configuration.getProperty("ldap.user"),
					Play.configuration.getProperty("ldap.password")
			);
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			String filter = "(&(objectCategory=Person)(objectClass=user))";
			NamingEnumeration<SearchResult> userEnum = ctx.search("", filter, ctls);
			while (userEnum.hasMoreElements()) {
				SearchResult userResult = userEnum.nextElement();
				String userName = (String) userResult.getAttributes().get("sAMAccountName").get();
				String displayName = (String) userResult.getAttributes().get("name").get();
				int userAccountControl = Integer.parseInt((String) userResult.getAttributes().get("userAccountControl").get());
				if (userAccountControl == 0 && displayName.contains("(") && displayName.contains(")")) {
					users.add(new User(userName, displayName, UserRole.user));
				}
			}
			ctx.close();
		} catch (NamingException e) {
			LOG.debug("", e);
		}

		return users;
	}
}
