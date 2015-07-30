package service;

import helper.MealType;
import helper.MonthlyOrderSummary;
import helper.OrderMenu;
import models.Menu;
import models.MenuItem;
import models.Order;
import models.User;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	public static void order(User user, Map<Long, Integer> orders) {
		if (null == user || orders == null || orders.isEmpty()) {
			throw new IllegalArgumentException();
		}

		for (Map.Entry<Long, Integer> item : orders.entrySet()) {
			final long menuItemId = null == item.getKey() ? -1 : item.getKey();
			final int quantity = null == item.getValue() ? -1 : item.getValue();

			if (quantity <= 0) {
				Order.delete("user.id=? AND menuItem.id=?", user.id, menuItemId);
			} else if (quantity > 0 && quantity <= 10) {
				Order o = Order.find("user.id=? AND menuItem.id=?", user.id, menuItemId).first();

				if (null == o) {
					o = new Order(user, MenuItem.<MenuItem>findById(menuItemId));
				}

				o.quantity = quantity;
				o.updatedAt = new Date();
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
