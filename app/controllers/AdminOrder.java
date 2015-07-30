package controllers;

import helper.OrderMenu;

import java.util.List;
import java.util.Map;

import models.Menu;
import models.User;
import play.data.validation.Required;
import play.mvc.With;
import service.OrderService;
import service.UserService;

@With(Secure.class)
@Check(User.ROLE_ADMIN)
public class AdminOrder extends Application {
	public static void importUsers() {
		UserService.importUsers();
		index(null);
	}

	public static void filterUser(String filter) {
		filter = filter.trim();
		index(filter.length() == 0 ? null : filter);
	}

	public static void index(String filter) {
		List<User> users = User.getUsers(filter);
		render(filter, users);
	}

	public static void show(Long userId) {
		User user = User.findById(userId);
		OrderMenu lunchOrderMenu = OrderService.getLunchOrderMenu(user);
		OrderMenu dinnerOrderMenu = OrderService.getDinnerOrderMenu(user);
		render(user, userId, lunchOrderMenu, dinnerOrderMenu);
	}

	public static void order(@Required Long userId, @Required Long menuId, Map<Long, Integer> orders) {
		User user = User.findById(userId);
		OrderService.order(user, orders);
		flash.success("order.success");
		show(userId);
	}
}
