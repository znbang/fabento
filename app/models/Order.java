package models;

import helper.MealType;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
@Table(name="orders")
public class Order extends Model {
	public String providerName;
	public String productName;
	public Integer productPrice;
	public Integer quantity;
	public Boolean paid;
	public Integer yearMonth;

	@Enumerated(EnumType.STRING)
	public MealType mealType;
	public Date createdAt;
	public Date updatedAt;

	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;

	@ManyToOne
	@JoinColumn(name="menu_id")
	public Menu menu;

	@ManyToOne
	@JoinColumn(name="menu_item_id")
	public MenuItem menuItem;

	public Order() {
	}

	public Order(User user, MenuItem menuItem) {
		DateTime now = new DateTime();

		this.createdAt = now.toDate();
		this.paid = false;
		this.user = user;
		this.menu = menuItem.menu;
		this.mealType = menuItem.menu.mealType;
		this.menuItem = menuItem;
		this.providerName = menuItem.providerName;
		this.productName = menuItem.productName;
		this.productPrice = menuItem.productPrice;

		// orders after 25th of the month belongs to next month
		if (now.dayOfMonth().get() > 25) {
			DateTime next = now.plusMonths(1);
			this.yearMonth = next.getYear() * 100 + next.getMonthOfYear();
		} else {
			this.yearMonth = now.getYear() * 100 + now.getMonthOfYear();
		}
	}

	@Transient
	public int getSubtotal() {
		return quantity * productPrice;
	}

	@Transient
	public static List<Integer> getYearMonthList(User user) {
		return find("SELECT DISTINCT(yearMonth) FROM Order WHERE mealType='LUNCH' AND user.id=?1 ORDER BY yearMonth DESC", user.id).fetch();
	}

	@Transient
	public static List<Order> getLunchOrders(User user, int yearMonth) {
		return find("mealType='LUNCH' AND user.id=?1 AND yearMonth=?2 ORDER BY createdAt", user.id, yearMonth).fetch();
	}
}
