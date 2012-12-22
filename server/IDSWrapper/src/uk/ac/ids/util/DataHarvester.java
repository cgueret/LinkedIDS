package uk.ac.ids.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataHarvester {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(DataHarvester.class
			.getName());

	// The URL to query
	private URL url = null;

	// The API key to use
	private String api_key = null;

	// The resultRoot
	private String resultRoot = null;

	/**
	 * @param url
	 */
	public void setURL(URL url) {
		this.url = url;
	}

	/**
	 * @param key
	 */
	public void setKey(String api_key) {
		this.api_key = api_key;
	}

	/**
	 * @return
	 * @throws IOException
	 */
	public Map<String, ArrayList<String>> getKeyValuePairs() throws IOException {
		Map<String, ArrayList<String>> results = new HashMap<String, ArrayList<String>>();

		// Issue the API request
		StringBuffer response = new StringBuffer();
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		if (api_key != null)
			connection.setRequestProperty("Token-Guid", api_key);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		String line;
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();

		// Parse the response
		JsonParser parser = new JsonParser();
		JsonElement element = parser.parse(response.toString());
		this.parseElement("", element, results);

		return results;
	}

	/**
	 * @param root
	 * @param element
	 * @param results
	 */
	private void parseElement(String root, JsonElement element,
			Map<String, ArrayList<String>> results) {
		if (element.isJsonObject()) {
			JsonObject obj = element.getAsJsonObject();
			for (Entry<String, JsonElement> entry : obj.entrySet()) {
				String newRoot = root + entry.getKey() + ".";
				parseElement(newRoot, entry.getValue(), results);
			}
		}

		else if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			for (int i = 0; i < array.size(); i++) {
				JsonElement v = array.get(i);
				String newRoot = root + i + ".";
				parseElement(newRoot, v, results);
			}
		}

		else if (element.isJsonPrimitive()) {
			String key = "#" + root.substring(0, root.length() - 1);
			if (resultRoot != null)
				key = key.replace(resultRoot + ".", "");
			ArrayList<String> list = null;
			if (results.containsKey(key)) {
				list = results.get(key);
			} else {
				list = new ArrayList<String>();
				results.put(key, list);
			}
			if (!element.getAsString().equals(""))
				list.add(element.getAsString());
		}
	}

	public static void main(String[] args) throws IOException {
		DataHarvester d = new DataHarvester();
		d.setURL(new URL(
				"http://oipa.openaidsearch.org//api/v2/activities/41AAA-00043782/?format=json"));
		Map<String, ArrayList<String>> r = d.getKeyValuePairs();
		for (Entry<String, ArrayList<String>> e : r.entrySet())
			System.out.println(e.getKey() + " = " + e.getValue());
	}

	public void setRoot(String resultRoot) {
		this.resultRoot = resultRoot;
	}
}
