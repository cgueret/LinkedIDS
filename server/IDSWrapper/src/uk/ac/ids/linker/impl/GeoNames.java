package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.restlet.data.Reference;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class GeoNames extends Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(GeoNames.class.getName());

	public static final String COUNTRY_NAME = "countryName";

	public static final String COUNTRY_CODE = "countryCode";

	// API to query geoname
	private final static String API = "http://api.geonames.org/search?";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected Reference getFromService(LinkerParameters parameters) {
		if (!parameters.containsKey(COUNTRY_NAME) || !parameters.containsKey(COUNTRY_CODE))
			return null;

		String countryName = parameters.get(COUNTRY_NAME);
		String countryCode = parameters.get(COUNTRY_CODE);

		StringBuffer urlString = new StringBuffer(API);
		urlString.append("name_equals=").append(countryName);
		urlString.append("&");
		urlString.append("country=").append(countryCode);
		urlString.append("&");
		urlString.append("username=idswrapper");
		urlString.append("&");
		urlString.append("type=json");

		try {
			// Compose the URL
			URL url = new URL(urlString.toString());

			// Issue the request
			StringBuffer response = new StringBuffer();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// Parse the response
			JsonParser parser = new JsonParser();
			JsonElement results = parser.parse(response.toString());
			if (results.isJsonObject()) {
				JsonObject obj = (JsonObject) results;
				if (obj.get("totalResultsCount").getAsInt() == 1) {
					JsonArray array = obj.get("geonames").getAsJsonArray();
					JsonObject entry = array.get(0).getAsJsonObject();
					String id = entry.get("geonameId").getAsString();
					return new Reference("http://sws.geonames.org/" + id);
				}
			}

			return null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
