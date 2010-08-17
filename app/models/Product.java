package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name="products")
public class Product extends Model {
	public String name;

	public Integer price;

	public Date createdAt;

	public Date updatedAt;

	@ManyToOne
	@JoinColumn(name="provider_id")
	@ForeignKey(name="fk_provider")
	public Provider provider;

	@OneToMany(mappedBy="product")
	public List<MenuItem> menuItems;
}
