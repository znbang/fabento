package models;

import helper.MealType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.joda.time.DateTime;


@Entity
@Table(name="menus")
public class Menu extends Model {
	@Column(unique=true)
	public String name;

	public String comment;

	@Enumerated(EnumType.STRING)
	public MealType mealType;

	public Date createdAt;

	public Date updatedAt;

	@OneToMany(mappedBy="menu", cascade=CascadeType.ALL)
	@OrderBy("providerName, productName")
	public List<MenuItem> menuItems;

	@OneToMany(mappedBy="menu")
	@OrderBy("providerName, productName")
	public List<Order> orders;

	public void addMenuItems(Provider provider) {
		for (Product product : provider.products) {
			MenuItem item = new MenuItem();
			item.menu = this;
			item.provider = provider;
			item.providerName = provider.name;
			item.product = product;
			item.productName = product.name;
			item.productPrice = product.price;
			menuItems.add(item);
		}
	}

	@Transient
	public boolean isOutdated() {
		if (name.indexOf("午餐") > 0 && new DateTime().withTime(10, 5, 0, 0).isBeforeNow()) {
			return true;
		} else if (name.indexOf("晚餐") > 0 && new DateTime().withTime(17, 5, 0, 0).isBeforeNow()) {
			return true;
		}
		return false;
	}

	@Transient
	public static List<Menu> listPastLunchMenu(int page, int rows) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return Menu.find("name < ? ORDER BY name DESC", sdf.format(new DateTime().plusDays(1).toDate())).from(--page * rows).fetch(rows);
	}

	@Transient
	public static List<Menu> listFutureLunchMenu() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return Menu.find("name > ? ORDER BY name", sdf.format(new DateTime().plusDays(1).toDate())).fetch();
	}
}
