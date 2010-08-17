package controllers;

import helper.MenuOrderSummary;
import helper.MonthlyOrderSummary;

import java.util.List;

import models.Menu;
import models.Order;
import models.User;

import org.joda.time.DateTime;

import play.data.validation.Required;
import play.mvc.With;
import service.OrderService;

@With(Secure.class)
@Check(User.ROLE_ADMIN)
public class OrderHistory extends Application {
	private static final int ROWS_PER_PAGE = 20;

	public static void index() {
		DateTime now = new DateTime();
		int firstYear = 2008;
		int firstMonth = 3;
		int lastYear = now.getYear();
		int currentMonth = now.getMonthOfYear();
		if (now.getDayOfMonth() > 25) {
			currentMonth++;
		}
		if (currentMonth > 12) {
			currentMonth = 1;
			lastYear++;
		}
		render(firstYear, firstMonth, lastYear, currentMonth);
	}

	public static void show(@Required Integer yearMonth) {
		int year = yearMonth / 100;
		int month = yearMonth % 100;
		MonthlyOrderSummary orders = OrderService.getOrderSummary(yearMonth);
		render(year, month, orders);
	}

	public static void everyday(Integer page) {
		page = null == page ? 1 : page;
		int prevPage = page > 1 ? page - 1 : 1;
		int nextPage = page + 1;
		List<models.Menu> menus = models.Menu.listPastLunchMenu(page, ROWS_PER_PAGE);
		if (menus.size() < ROWS_PER_PAGE) {
			nextPage--;
		}
		render(menus, prevPage, nextPage);
	}

	public static void showMenuOrder(Long menuId) {
		Menu menu = Menu.findById(menuId);
		MenuOrderSummary summary = new MenuOrderSummary(menu);
		render(menu, summary);
	}

	public static void makeOrderPaid(Long orderId) {
		Order order = Order.findById(orderId);
		order.paid = Boolean.TRUE;
		order.save();
		showMenuOrder(order.menu.id);
	}

	public static void makeOrderUnpaid(Long orderId) {
		Order order = Order.findById(orderId);
		order.paid = Boolean.FALSE;
		order.save();
		showMenuOrder(order.menu.id);
	}
}
