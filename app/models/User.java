package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="users")
public class User extends Model {
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_USER = "user";
	public static final String ROLE_GUEST = "guest";
	public static final User GUEST = new User("guest", "Guest", ROLE_GUEST);

	public String userName;
	public String displayName;
	public String role;
	public Integer enabled;

	public User(String userName, String displayName, String role) {
		this.userName = userName.toLowerCase();
		this.displayName = displayName;
		this.role = role;
		this.enabled = 1;
	}

	public boolean isSignedIn() {
		return ROLE_USER.equals(role) || ROLE_ADMIN.equals(role);
	}

	public boolean isAdmin() {
		return ROLE_ADMIN.equals(role);
	}

	public boolean isUser() {
		return ROLE_USER.equals(role);
	}

	public boolean isGuest() {
		return ROLE_GUEST.equals(role);
	}

	@Transient
	public static List<User> getAdmins() {
		return find("enabled=1 AND role='admin' ORDER BY userName").fetch();
	}

	@Transient
	public static List<User> getUsers(String filter) {
		String myFilter = "%" + filter + "%";
		return null == filter ? User.find("enabled=1 ORDER BY userName").<User>fetch() : User.find("enabled=1 AND (userName LIKE ? OR displayName LIKE ?)", myFilter, myFilter).<User>fetch();
	}

	public static User findByUserName(String userName) {
		return find("userName=? AND enabled=1", userName).first();
	}
}
