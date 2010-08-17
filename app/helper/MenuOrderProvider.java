package helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import models.Order;

public class MenuOrderProvider implements Serializable {
	private int quantity;

	private int subtotal;

	private List<MenuOrderSummaryItem> items = new ArrayList<MenuOrderSummaryItem>();

	public void addOrder(Order order) {
		this.quantity += order.quantity;
		this.subtotal += order.getSubtotal();
		addItem(order);
	}

	private void addItem(Order order) {
		MenuOrderSummaryItem item = findItem(order.providerName, order.productName);
		if (null == item) {
			item = new MenuOrderSummaryItem(order.providerName, order.productName, order.productPrice);
			items.add(item);
		}
		item.addQuantity(order.quantity);
		if (order.paid != null && order.paid.booleanValue()) {
			item.addPaidUser(order);
		} else {
			item.addUnpaidUser(order);
		}
	}

	private MenuOrderSummaryItem findItem(String provider, String product) {
		MenuOrderSummaryItem item = null;
		for (MenuOrderSummaryItem a : items) {
			if (a.getProvider().equals(provider) && a.getProduct().equals(product)) {
				item = a;
				break;
			}
		}
		return item;
	}

	public int getQuantity() {
		return quantity;
	}

	public int getSubtotal() {
		return subtotal;
	}

	public List<MenuOrderSummaryItem> getItems() {
		return items;
	}
}
