package recommendation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

public class GeoRecommendation {
	public List<Item> recommendItems(String userId, double lat, double lon) {
		
		List<Item> recommendationItems = new ArrayList<>();
		DBConnection conn = DBConnectionFactory.getConnection();

		// step 1: for each user liked item, get the category and count the number
		Set<String> favoriteItemIds = conn.getFavoriteItemIds(userId);
		Map<String, Integer> categoriesCounter = new HashMap<>();
		for (String itemId : favoriteItemIds) {
			Set<String> categories = conn.getCategories(itemId);
			for (String category : categories) {
				categoriesCounter.put(category, categoriesCounter.getOrDefault(category, 0) + 1);
			}
		}

		// Step 2: Sort the category
		List<Entry<String, Integer>> categoryList = new ArrayList<>(categoriesCounter.entrySet());
		Collections.sort(categoryList, (Entry<String, Integer> o1, Entry<String, Integer> o2) -> {
			return Integer.compare(o1.getValue(), o2.getValue());
		});

		// step 3: for each category, search the item using TicketMaster API
		Set<String> visited = new HashSet<>();
		for (Entry<String, Integer> category : categoryList) {
			List<Item> items = conn.searchItems(lat, lon, category.getKey());
			for (Item item : items) {
				// if not the same item liked before
				if (!favoriteItemIds.contains(item.getItemId()) && !visited.contains(item.getItemId())) {
					recommendationItems.add(item);
					visited.add(item.getItemId());
				}
			}
		}
		conn.close();
		return recommendationItems;
	}
}
