package models;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name="users")
public class User extends Model {
	public static final String ROLE_ADMIN = "admin";
	public static final String ROLE_USER = "user";
	public static final String ROLE_GUEST = "guest";
	public static final User GUEST = new User("guest", "Guest", UserRole.guest);

	public String userName;
	public String displayName;
	@Enumerated(EnumType.STRING)
	public UserRole role;
	public Integer enabled;

	public User(String userName, String displayName, UserRole role) {
		this.userName = userName.toLowerCase();
		int left  = displayName.indexOf('(');
		int right = displayName.indexOf(')');
		if (left > 0 && right > 0) {
			this.displayName = displayName.substring(left + 1, right);
		} else {
			this.displayName = displayName;
		}
		this.role = role;
		this.enabled = 1;
	}

	public boolean isSignedIn() {
		return UserRole.user == role || UserRole.admin == role;
	}

	public boolean isAdmin() {
		return UserRole.admin == role;
	}

	public boolean isUser() {
		return UserRole.admin == role;
	}

	public boolean isGuest() {
		return UserRole.guest == role;
	}

	@Transient
	public static List<User> getAdmins() {
		return find("enabled=1 AND role='admin' ORDER BY userName").fetch();
	}

	@Transient
	public static List<User> getUsers(String filter) {
		String myFilter = "%" + filter + "%";
		return null == filter ? User.find("enabled=1 ORDER BY userName").<User>fetch() : User.find("enabled=1 AND (userName LIKE ?1 OR displayName LIKE ?2)", myFilter, myFilter).fetch();
	}

	public static User findByUserName(String userName) {
		return find("userName=?1 AND enabled=1", userName).first();
	}
}
