package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcHelper {
	// Serilize the JSONArray to string to http response.
	public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(array);
		out.close();
	}

    // Serilize the JSON object to string to http response.
	public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		PrintWriter out = response.getWriter();
		out.print(obj);
		out.close();
	}
	
	// Read from http request and deserilize the string to get a JSON object
	public static JSONObject toJsonObject(HttpServletRequest request) {		
		StringBuilder sBuilder = new StringBuilder();
		try {
			BufferedReader reader = request.getReader();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sBuilder.append(line);
			}
			return new JSONObject(sBuilder.toString());
		} catch (Exception e) {
			log.error("error deserilize JSON");
		}
		return new JSONObject();
	}
	
	// convert a list of item to a JSON array
	public static JSONArray getJSONArray(List<Item> items) {
		JSONArray result = new JSONArray();
		try {
			for (Item item : items) {
				result.put(item.toJSONObject());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
