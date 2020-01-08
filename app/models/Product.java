package models;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="products")
public class Product extends Model {
	public String name;
	public Integer price;
	public Date createdAt;
	public Date updatedAt;

	@ManyToOne
	@JoinColumn(name="provider_id")
	public Provider provider;

	@OneToMany(mappedBy="product")
	public List<MenuItem> menuItems;
}
