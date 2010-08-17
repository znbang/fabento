package helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import models.Menu;
import models.MenuItem;
import models.Order;

public class OrderMenu implements Serializable {
	private Long menuId;

	private String name;

	private String comment;

	private List<OrderMenuItem> items;

	public OrderMenu(Menu menu, List<Order> orders) {
		this.menuId = menu.id;
		this.name = menu.name;
		this.comment = menu.comment;
		initItems(menu, orders);
	}

	private void initItems(Menu menu, List<Order> orders) {
		items = new ArrayList<OrderMenuItem>();
		for (MenuItem a : menu.menuItems) {
			items.add(createOrderMenuItem(a, orders));
		}
	}

	private OrderMenuItem createOrderMenuItem(MenuItem menuItem, List<Order> orders) {
		int quantity = 0;
		for (Order a : orders) {
			if (menuItem.equals(a.menuItem)) {
				quantity = a.quantity;
				break;
			}
		}
		return new OrderMenuItem(menuItem.id, menuItem.providerName, menuItem.productName, menuItem.productPrice, quantity);
	}

	public Long getMenuId() {
		return menuId;
	}

	public String getName() {
		return name;
	}

	public String getComment() {
		return comment;
	}

	public List<OrderMenuItem> getItems() {
		return items;
	}

	public int getTotalQuantity() {
		int sum = 0;
		for (OrderMenuItem a : items) {
			sum += a.getQuantity();
		}
		return sum;
	}

	public int getTotalPrice() {
		int sum = 0;
		for (OrderMenuItem a : items) {
			sum += a.getQuantity() * a.getPrice();
		}
		return sum;
	}
}
