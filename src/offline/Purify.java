package offline;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bson.Document;

import db.mongodb.MongoDBUtil;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class Purify {
	public static void main(String[] args) {
		MongoClient mongoClient = new MongoClient();
		MongoDatabase db = mongoClient.getDatabase(MongoDBUtil.DB_NAME);

		String path = "C:\\Users\\chaor\\Downloads\\tomcat_log.txt";

		try {
			// drop collection if exists
			db.getCollection("logs").drop();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				List<String> values = Arrays.asList(line.split(" "));

				String ip = values.size() > 0 ? values.get(0) : null;
				String timestamp = values.size() > 3 ? values.get(3) : null;
				String method = values.size() > 5 ? values.get(5) : null;
				String url = values.size() > 6 ? values.get(6) : null;
				String status = values.size() > 8 ? values.get(8) : null;

				Pattern pattern = Pattern.compile("\\[(.+?):(.+)");
				Matcher matcher = pattern.matcher(timestamp);
				matcher.find();

				db.getCollection("logs")
				.insertOne(new Document().append("ip", ip).append("date", matcher.group(1))
						.append("time", matcher.group(2)).append("method", method.substring(1))
						.append("url", url).append("status", status));
			}
			System.out.println("Import Done!");
			bufferedReader.close();
			mongoClient.close();


		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}
