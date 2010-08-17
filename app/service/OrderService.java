package service;

import helper.MealType;
import helper.MonthlyOrderSummary;
import helper.OrderMenu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Menu;
import models.MenuItem;
import models.Order;
import models.User;

import org.joda.time.DateTime;

public class OrderService {
	public static OrderMenu getLunchOrderMenu(User user) {
		return getOrderMenu(user, " 午餐");
	}

	public static OrderMenu getDinnerOrderMenu(User user) {
		return getOrderMenu(user, " 加班晚餐");
	}

	private static OrderMenu getOrderMenu(User user, String name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Menu menu = Menu.find("name", sdf.format(new Date()) + name).first();
		return null == menu ? null : new OrderMenu(menu, Order.find("user.id=? AND menu.id=?", user.id, menu.id).<Order>fetch());
	}

	public static void order(User user, Long[] items, Integer[] quantities) {
		if (null == user || items.length != quantities.length) {
			throw new IllegalArgumentException();
		}

		int length = items.length;

		for (int i = 0; i < length; ++i) {
			final long menuItemId = null == items[i] ? -1 : items[i];
			final int quantity = null == quantities[i] ? -1 : quantities[i];

			if (quantity <= 0) {
				Order.delete("user.id=? AND menuItem.id=?", user.id, menuItemId);
			} else if (quantity > 0 && quantity <= 10) {
				DateTime now = new DateTime();
				Order o = Order.find("user.id=? AND menuItem.id=?", user.id, menuItemId).first();

				if (null == o) {
					MenuItem menuItem = MenuItem.findById(menuItemId);

					o = new Order();
					o.createdAt = now.toDate();
					o.paid = false;
					o.user = user;
					o.menu = menuItem.menu;
					o.mealType = menuItem.menu.mealType;
					o.menuItem = menuItem;

					// orders after 25th of the month belongs to next month
					if (now.dayOfMonth().get() > 25) {
						DateTime next = now.plusMonths(1);
						o.yearMonth = next.getYear() * 100 + next.getMonthOfYear();
					} else {
						o.yearMonth = now.getYear() * 100 + now.getMonthOfYear();
					}
				}

				o.quantity = quantity;
				o.updatedAt = now.toDate();
				o.save();
			}
		}
	}

	public static MonthlyOrderSummary getOrderSummary(int yearMonth) {
		MonthlyOrderSummary summary = new MonthlyOrderSummary();
		List<Object[]> rows = Order.find("SELECT u.userName, u.displayName, SUM(o.quantity), SUM (o.productPrice * o.quantity) FROM Order o INNER JOIN o.user u WHERE o.mealType=? AND o.yearMonth=? GROUP BY u.userName, u.displayName ORDER BY u.userName", MealType.LUNCH, yearMonth).fetch();
		for (Object[] a : rows) {
			String userName = (String)a[0];
			String displayName = (String)a[1];
			Number quantity = (Number)a[2];
			Number subtotal = (Number)a[3];
			summary.addItem(userName, displayName, quantity.intValue(), subtotal.intValue());
		}
		return summary;
	}
}
