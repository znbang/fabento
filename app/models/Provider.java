package models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="providers")
public class Provider extends Model {
	@Column(unique=true)
	public String name;
	public String phone;
	public String address;
	public Date createdAt;
	public Date updatedAt;
	public Integer status;

	@OneToMany(mappedBy="provider", cascade=CascadeType.ALL)
	public List<Product> products;

	@OneToMany(mappedBy="provider")
	public List<MenuItem> menuItems;

	public void addProduct(String name, int price) {
		Product product = new Product();
		product.provider = this;
		product.name = name;
		product.price = price;
		product.createdAt = new Date();
		product.updatedAt = product.createdAt;
		products.add(product);
	}

	public static List<Provider> list() {
		return find("status IS NULL ORDER BY name").fetch();
	}

	public static List<Provider> list(int page, int rows) {
		return find("status IS NULL ORDER BY name").from(--page * rows).fetch(rows);
	}
}
