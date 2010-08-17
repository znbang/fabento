package helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Order;

public class MenuOrderSummaryItem implements Serializable {
	private String provider;

	private String product;

	private int price;

	private int quantity;

	private List<MenuOrderSummaryUser> paidUsers = new ArrayList<MenuOrderSummaryUser>();

	private List<MenuOrderSummaryUser> unpaidUsers = new ArrayList<MenuOrderSummaryUser>();

	public MenuOrderSummaryItem(String provider, String product, int price) {
		this.provider = provider;
		this.product = product;
		this.price = price;
	}

	public String getProvider() {
		return provider;
	}

	public String getProduct() {
		return product;
	}

	public int getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void addQuantity(int quantity) {
		this.quantity += quantity;
	}

	public void addPaidUser(Order order) {
		paidUsers.add(new MenuOrderSummaryUser(order.getId(), order.user.userName, order.user.displayName, order.quantity));
	}

	public void addUnpaidUser(Order order) {
		unpaidUsers.add(new MenuOrderSummaryUser(order.getId(), order.user.userName, order.user.displayName, order.quantity));
	}

	public List<MenuOrderSummaryUser> getPaidUsers() {
		Collections.sort(paidUsers);
		return paidUsers;
	}

	public List<MenuOrderSummaryUser> getUnpaidUsers() {
		Collections.sort(unpaidUsers);
		return unpaidUsers;
	}
}
