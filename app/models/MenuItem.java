package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="menu_items")
public class MenuItem extends Model {
	public String providerName;

	public String productName;

	public Integer productPrice;

	@ManyToOne
	@JoinColumn(name="menu_id")
	@ForeignKey(name="fk_menu")
	public Menu menu;

	@ManyToOne
	@JoinColumn(name="provider_id")
	@ForeignKey(name="fk_provider")
	public Provider provider;

	@ManyToOne
	@JoinColumn(name="product_id")
	@ForeignKey(name="fk_product")
	public Product product;

	@OneToMany(mappedBy="menuItem")
	public List<Order> orders;

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof MenuItem) {
			MenuItem a = (MenuItem)obj;
			return providerName.equals(a.providerName) &&
				   productName.equals(a.productName) &&
				   productPrice.equals(a.productPrice);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (providerName + productName + productPrice).hashCode();
	}
}
