package uk.ac.ids.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.restlet.data.Reference;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeoNamesBrowser {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(GeoNamesBrowser.class.getName());

	// Entity type in the AppEngine data store
	private final static String RESOURCE_ENTITY = "GeoNameResource";

	// API to query geoname
	private final static String API = "http://api.geonames.org/search?";

	/**
	 * @param countryName
	 * @param countryCode
	 * @return
	 */
	public Reference getResource(String countryName, String countryCode) {
		logger.info("Get " + countryName + " in " + countryCode);
		try {
			// Try to return the result from the cache
			return getFromCache(countryName, countryCode);
		} catch (EntityNotFoundException e) {
			// Try to get it from geoname
			Reference uri = getFromGeoname(countryName, countryCode);
			
			// If successful, save it
			if (uri != null) {
				saveToCache(countryName, countryCode, uri);
			}
			
			return uri;
		}

	}


	/**
	 * @param countryName
	 * @param countryCode
	 * @return
	 */
	private Reference getFromGeoname(String countryName, String countryCode) {
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

	/**
	 * @param countryName
	 * @param countryCode
	 * @return
	 * @throws EntityNotFoundException
	 */
	private Reference getFromCache(String countryName, String countryCode) throws EntityNotFoundException {
		StringBuffer keyStringBuffer = new StringBuffer();
		keyStringBuffer.append(countryName).append("_").append(countryCode);
		String keyString = keyStringBuffer.toString();

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(RESOURCE_ENTITY, keyString);
		Entity entity = datastore.get(k);
		return new Reference((String) entity.getProperty("uri"));
	}

	/**
	 * @param countryName
	 * @param countryCode
	 * @param uri
	 */
	private void saveToCache(String countryName, String countryCode, Reference uri) {
		StringBuffer keyStringBuffer = new StringBuffer();
		keyStringBuffer.append(countryName).append("_").append(countryCode);
		String keyString = keyStringBuffer.toString();
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity entity = null;
		try {
			Key k = KeyFactory.createKey(RESOURCE_ENTITY, keyString);
			entity = datastore.get(k);
		} catch (EntityNotFoundException e) {
			entity = new Entity(RESOURCE_ENTITY, keyString);
		}
		entity.setProperty("uri", uri.toString());
		datastore.put(entity);
	}
}
