package controllers;

import java.util.Date;
import java.util.List;

import models.Product;
import models.User;
import play.data.validation.Required;
import play.mvc.With;

@With(Secure.class)
@Check(User.ROLE_ADMIN)
public class Provider extends Application {
	private static final int ROWS_PER_PAGE = 20;

	public static void index(Integer page) {
		page = null == page ? 1 : page;
		int prevPage = page > 1 ? page - 1 : 1;
		int nextPage = page + 1;
		List<models.Provider> providers = models.Provider.list(page, ROWS_PER_PAGE);
		if (providers.size() < ROWS_PER_PAGE) {
			nextPage--;
		}
		render(prevPage, nextPage, providers);
	}

	public static void newProvider() {
		render();
	}

	public static void createProvider(@Required String name, String phone, String address) {
		name = name.trim();
		if (name.length() > 0) {
			Date now = new Date();
			models.Provider a = new models.Provider();
			a.name = name;
			a.phone = phone;
			a.address = address;
			a.createdAt = now;
			a.updatedAt = now;
			a.save();
			editProvider(a.id);
		}
		index(null);
	}

	public static void editProvider(Long id) {
		models.Provider provider = models.Provider.findById(id);
		if (null == provider) {
			index(null);
		} else {
			render(provider);
		}
	}

	public static void updateProvider(Long id, String name, String phone, String address, Long[] deletes, Long[] productIds, String[] productNames, Integer[] productPrices, String newProductName, Integer newProductPrice) {
		models.Provider provider = models.Provider.findById(id);
		if (null == provider) {
			index(null);
		} else {
			provider.name = name;
			provider.phone = phone;
			provider.address = address;
			provider.save();

			// delete products
			if (deletes != null) {
				for (Long productId : deletes) {
					Product a = Product.findById(productId);
					if (a != null) {
						a.delete();
					}
				}
			}

			// update products
			if (productIds != null) {
				int count = productIds.length;
				for (int i = 0; i < count; ++i) {
					Product a = Product.findById(productIds[i]);
					if (a != null) {
						a.name = null == productNames[i] ? a.name : productNames[i];
						a.price = null == productPrices[i] ? a.price : productPrices[i];
						a.updatedAt = new Date();
						a.save();
					}
				}
			}

			// create product
			newProductName = newProductName.trim();
			if (newProductName.length() > 0) {
				Product a = new Product();
				a.name = newProductName;
				a.price = null == newProductPrice ? 0 : newProductPrice;
				a.createdAt = new Date();
				a.updatedAt = new Date();
				a.provider = provider;
				a.save();
			}
		}
		editProvider(id);
	}

	public static void deleteProvider(Long id) {
		models.Provider a = models.Provider.findById(id);
		if (null != a) {
			a.status = 1;
			a.save();
		}
		index(null);
	}
}
