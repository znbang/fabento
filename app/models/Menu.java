package models;

import helper.MealType;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


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
			item.save();
			menuItems.add(item);
		}
	}

	@Transient
	public boolean isOutdated() {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(name);
		} catch (ParseException e) {
			return true;
		}
		DateTime validTime = new DateTime(date);
		if (mealType == MealType.LUNCH) {
			return validTime.withTime(10, 5, 0, 0).isBeforeNow();
		} else if (mealType == MealType.DINNER) {
			return validTime.withTime(17, 5, 0, 0).isBeforeNow();
		}
		return true;
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
