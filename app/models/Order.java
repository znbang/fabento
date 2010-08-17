package models;

import helper.MealType;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ForeignKey;


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
	@ForeignKey(name="fk_user")
	public User user;

	@ManyToOne(optional=true)
	@JoinColumn(name="menu_id")
	@ForeignKey(name="fk_menu")
	public Menu menu;

	@ManyToOne(optional=true)
	@JoinColumn(name="menu_item_id")
	@ForeignKey(name="fk_menu_item")
	public MenuItem menuItem;


	public void setMenuItem(MenuItem menuItem) {
		this.menuItem = menuItem;
		providerName = menuItem.providerName;
		productName = menuItem.productName;
		productPrice = menuItem.productPrice;
	}

	@Transient
	public int getSubtotal() {
		return quantity * productPrice;
	}

	@Transient
	public static List<Integer> getYearMonthList(User user) {
		return find("SELECT DISTINCT(yearMonth) FROM Order WHERE mealType='LUNCH' AND user.id=? ORDER BY yearMonth DESC", user.id).fetch();
	}

	@Transient
	public static List<Order> getLunchOrders(User user, int yearMonth) {
		return find("mealType='LUNCH' AND user.id=? AND yearMonth=? ORDER BY createdAt", user.id, yearMonth).fetch();
	}
}
