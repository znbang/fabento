package helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Menu;
import models.Order;

public class MenuOrderSummary implements Serializable {
	private String menuName;

	private Map<String, MenuOrderProvider> summaries = new HashMap<String, MenuOrderProvider>();

	public MenuOrderSummary(Menu menu) {
		menuName = menu.name;
		for (Order a : menu.orders) {
			addOrder(a);
		}
	}

	private void addOrder(Order order) {
		MenuOrderProvider summary = summaries.get(order.providerName);
		if (null == summary) {
			summary = new MenuOrderProvider();
			summaries.put(order.providerName, summary);
		}
		summary.addOrder(order);
	}

	public String getMenuName() {
		return menuName;
	}

	public List<MenuOrderProvider> getSummaries() {
		return new ArrayList<MenuOrderProvider>(summaries.values());
	}

	public int getTotalQuantity() {
		int sum = 0;
		for (MenuOrderProvider a : summaries.values()) {
			sum += a.getQuantity();
		}
		return sum;
	}

	public int getTotalPrice() {
		int sum = 0;
		for (MenuOrderProvider a : summaries.values()) {
			sum += a.getSubtotal();
		}
		return sum;
	}
}
