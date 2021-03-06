package controllers;

import helper.OrderMenu;
import models.Menu;
import models.Order;
import models.User;
import play.data.validation.Required;
import play.mvc.With;
import service.OrderService;

import java.util.List;
import java.util.Map;

@With(RequireLogin.class)
public class UserOrder extends Controller {
	public static void index() {
		User user = getCurrentUser();
		OrderMenu lunchOrderMenu = OrderService.getLunchOrderMenu(user);
		OrderMenu dinnerOrderMenu = OrderService.getDinnerOrderMenu(user);
		render(lunchOrderMenu, dinnerOrderMenu);
	}

	public static void order(@Required Long menuId, Map<Long, Integer> orders) {
		if (Menu.<Menu>findById(menuId).isOutdated()) {
			flash.error("order.outdated");
		} else {
			User user = getCurrentUser();
			OrderService.order(user, orders);
			flash.success("order.success");
		}
		index();
	}

	public static void history() {
		User user = getCurrentUser();
		List<Integer> yearMonthList = Order.getYearMonthList(user);
		render(yearMonthList);
	}

	public static void show(@Required Integer yearMonth) {
		int year = yearMonth / 100;
		int month = yearMonth % 100;
		int totalQuantity = 0;
		int totalPrice = 0;
		User user = getCurrentUser();
		List<Order> orders = Order.getLunchOrders(user, yearMonth);
		for (Order order : orders) {
			totalQuantity += order.quantity;
			totalPrice += order.getSubtotal();
		}
		render(year, month, orders, totalQuantity, totalPrice);
	}

	public static void futureMenu() {
		List<models.Menu> menus = models.Menu.listFutureLunchMenu();
		render(menus);
	}

	public static void showMenu(Long id) {
		models.Menu menu = models.Menu.findById(id);
		if (menu != null) {
			render(menu);
		} else {
			futureMenu();
		}
	}
}
