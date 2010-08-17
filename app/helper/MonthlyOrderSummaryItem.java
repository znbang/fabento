package helper;

import java.io.Serializable;

public class MonthlyOrderSummaryItem implements Serializable {
	private String userName;

	private String displayName;

	private int quantity;

	private int subtotal;

	public MonthlyOrderSummaryItem(String userName, String displayName, int quantity, int subtotal) {
		this.userName = userName;
		this.displayName = displayName;
		this.quantity = quantity;
		this.subtotal = subtotal;
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

	public int getSubtotal() {
		return subtotal;
	}
}
