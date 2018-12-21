package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "wgVYVDI533Wou3yfFFv3SJJqyr7yOLeS";

	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}

		// "Rick Sun" => "Rick%20Sun"
		// URL can not contain space because HTTP use space to seperate the Request Line
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("Error encoding key word", e);
		}
		
		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);

		// apikey=abcde&geoPoint=xyz123&keyword=&radius=50
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 50);
		String url = URL + "?" + query;

		try {
			// create connection
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			int status = conn.getResponseCode();
			if (status != 200) {
				log.debug("Ticket Master not response 200");
				return new ArrayList<>();
			}
			
			// read string data from input stream
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line = null;
			StringBuilder response = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			
			// create JSON object from JSON string
			JSONObject obj = new JSONObject(response.toString());
			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				return getItemList(embedded.getJSONArray("events"));
			}
		} catch (Exception e) {
			log.error("error search from ticket master", e);
		}
		log.debug("returned empty list from search");
		return new ArrayList<>();
	}

	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = Item.builder();
			if (!event.isNull("id")) {
				builder.itemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.name(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.url(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.distance(event.getDouble("distance"));
			}
			if (!event.isNull("rating")) {
				builder.rating(event.getDouble("rating"));
			}
			builder.address(getAddress(event));
			builder.categories(getCategories(event));
			builder.imageUrl(getImageUrl(event));
			itemList.add(builder.build());
		}
		return itemList;
	}
	
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sBuilder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							sBuilder.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							sBuilder.append(",");
							sBuilder.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							sBuilder.append(",");
							sBuilder.append(address.getString("line3"));
						}
					}
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							sBuilder.append(",");
							sBuilder.append(city.getString("name"));
						}
					}
					if (!sBuilder.toString().equals("")) {
						return sBuilder.toString();
					}
				}
				
			}
		}
		log.debug("returned empty string from getAddress()");
		return "";
	}
	
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); ++i) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		log.debug("returned empty image url");
		return "";
	}
	
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	/*
	// Test TicketMaster API
	public static void main(String[] args) {
		TicketMasterAPI api = new TicketMasterAPI();
		List<Item> items = api.search(37.38, -122.08, null);
		for (Item item : items) {
			System.out.println(item.toJSONObject());
		}
	}
	*/
}
