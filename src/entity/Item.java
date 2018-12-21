package entity;

import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;;

@Getter
@Builder
@Slf4j
public class Item {
	final private String itemId;
	final private String name;
	final private double rating;
	final private String address;
	final private Set<String> categories;
	final private String imageUrl;
	final private String url;
	final private double distance;
		
	// from item object to json object, then we can serilize object
	// to string using the library and write to response
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		// use JSONObject put() method to create json object
		// can written by hand => new JSONObject("JSON String")
		try {
			obj.put("item_id", itemId);
			obj.put("name", name);
			obj.put("rating", rating);
			obj.put("address", address);
			obj.put("categories", new JSONArray(categories));
			obj.put("image_url", imageUrl);
			obj.put("url", url);
			obj.put("distance", distance);
		} catch (JSONException e) {
			log.error("error parse Item object to JSON object");
		}
		return obj;
	}
}
