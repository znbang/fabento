package helper;

import java.io.Serializable;

public class OrderMenuItem implements Serializable {
	private Long menuItemId;

	private String provider;

	private String product;

	private int price;

	private int quantity;

	public OrderMenuItem(Long menuItemId, String provider, String product, int price, int quantity) {
		this.menuItemId = menuItemId;
		this.provider = provider;
		this.product = product;
		this.price = price;
		this.quantity = quantity;
	}

	public Long getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(Long menuItemId) {
		this.menuItemId = menuItemId;
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

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
