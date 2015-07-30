package controllers;

import helper.MealType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.MenuItem;
import models.Provider;
import models.User;
import play.mvc.With;

@With(Secure.class)
@Check(User.ROLE_ADMIN)
public class Menu extends Application {
	private static final int ROWS_PER_PAGE = 20;
	private static final String LUNCH = "午餐";
	private static final String DINNER = "加班晚餐";

	public static void index(Integer page) {
		page = null == page ? 1 : page;
		int prevPage = page > 1 ? page - 1 : 1;
		int nextPage = page + 1;
		List<models.Menu> menus = models.Menu.find("ORDER BY name DESC").from(--page * ROWS_PER_PAGE).fetch(ROWS_PER_PAGE);
		if (menus.size() < ROWS_PER_PAGE) {
			nextPage--;
		}
		render(menus, prevPage, nextPage);
	}

	public static void editMenu(Long id) {
		models.Menu menu = models.Menu.findById(id);
		List<Provider> providers = Provider.list();
		render(menu, providers);
	}

	public static void updateMenu(Long id, String comment, Long[] deletes, Long[] itemIds, String[] productNames, Integer[] productPrices, Long newProvider) {
		models.Menu menu = models.Menu.findById(id);
		if (null == menu) {
			index(null);
		} else {
			menu.comment = comment;
			menu.updatedAt = new Date();
			menu.save();

			// delete items
			if (deletes != null) {
				for (Long itemId : deletes) {
					MenuItem a = MenuItem.findById(itemId);
					if (a != null) {
						a.delete();
					}
				}
			}

			// update items
			if (itemIds != null) {
				int count = itemIds.length;
				for (int i = 0; i < count; ++i) {
					MenuItem a = MenuItem.findById(itemIds[i]);
					if (a != null) {
						a.productName = null == productNames[i] ? a.productName : productNames[i];
						a.productPrice = null == productPrices[i] ? a.productPrice : productPrices[i];
						a.save();
					}
				}
			}

			// new provider
			if (newProvider != null) {
				Provider a = Provider.findById(newProvider);
				if (a != null) {
					menu.addMenuItems(a);
				}
			}

			editMenu(id);
		}
	}

	public static void deleteMenu(Long id) {
		models.Menu a = models.Menu.findById(id);
		if (a != null) {
			a.delete();
		}
		index(null);
	}

	public static void newMenu() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String today = sdf.format(new Date());
		render(today);
	}

	public static void createMenu(String date, String menuType, String comment) {
		date = date.trim();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			sdf.parse(date);
		} catch (ParseException e) {
			newMenu();
		}
		models.Menu menu = new models.Menu();
		if ("lunch".equals(menuType)) {
			menu.name = date + " " + LUNCH;
			menu.mealType = MealType.LUNCH;
		} else {
			menu.name = date + " " + DINNER;
			menu.mealType = MealType.DINNER;
		}
		menu.comment = comment;
		menu.createdAt = new Date();
		menu.updatedAt = new Date();
		menu.save();
		editMenu(menu.id);
	}
}
