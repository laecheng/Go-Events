package rpc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import entity.Item;

class RpcHelperTest {

	/**
	 * Test RpcHelper getJSONArray
	 * @throws JSONException
	 */
	@Test
	public void testGetJSONArray() throws JSONException {
		Set<String> category = new HashSet<String>();
		category.add("category one");
		Item one = Item.builder().itemId("one").name("item one").rating(4).distance(15).categories(category).build();
		Item two = Item.builder().itemId("two").name("item two").rating(3).distance(10).categories(category).build();

		List<Item> listItem = new ArrayList<Item>();
		listItem.add(one);
		listItem.add(two);
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(one.toJSONObject());
		jsonArray.put(two.toJSONObject());
		
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
	}

	
	/**
	 * Test RpcHelper getJSONArray Corner case: empty item
	 * @throws JSONException
	 */
	@Test
	public void testGetJSONArrayEmptyItem() throws JSONException {
		Set<String> category = new HashSet<String>();
		category.add("category one");
		Item one = Item.builder().itemId("one").name("item one").rating(4).distance(15).categories(category).build();
		Item two = Item.builder().itemId("two").name("item two").rating(3).distance(10).categories(category).build();
		Item empty = Item.builder().build();
		
		List<Item> listItem = new ArrayList<Item>();
		listItem.add(one);
		listItem.add(two);
		listItem.add(empty);
		
		JSONArray jsonArray = new JSONArray();
		jsonArray.put(one.toJSONObject());
		jsonArray.put(two.toJSONObject());
		jsonArray.put(empty.toJSONObject());
		
		JSONAssert.assertEquals(jsonArray, RpcHelper.getJSONArray(listItem), true);
	}
}
