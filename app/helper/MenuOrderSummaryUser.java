package helper;

import java.io.Serializable;

public class MenuOrderSummaryUser implements Comparable<MenuOrderSummaryUser>, Serializable {
	private Long orderId;

	private String userName;

	private String displayName;

	private int quantity;

	public MenuOrderSummaryUser(Long orderId, String userName, String displayName, int quantity) {
		this.orderId = orderId;
		this.userName = userName;
		this.displayName = displayName;
		this.quantity = quantity;
	}

	public Long getOrderId() {
		return orderId;
	}

	public String getUserName() {
		return userName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public int getQuantity() {
		return quantity;
	}

	public int compareTo(MenuOrderSummaryUser o) {
		return userName.compareTo(o.userName);
	}
}
