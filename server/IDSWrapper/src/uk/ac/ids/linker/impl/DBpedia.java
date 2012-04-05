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
public class DBpedia extends Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(DBpedia.class.getName());

	// Parameters
	public static final String THEME_TITLE = "title";

	// API to query geoname
	private final static String API = "http://dbpedia.org/sparql?";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected Reference getFromService(LinkerParameters parameters) {
		if (!parameters.containsKey(THEME_TITLE))
			return null;

		String themeTitle = parameters.get(THEME_TITLE);

		// Build the sparql query
		String sparqlQuery = "select distinct ?Concept where  {?Concept <http://www.w3.org/2000/01/rdf-schema#label> \"";
		sparqlQuery += themeTitle;
		sparqlQuery += "\"@en . ?Concept <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.w3.org/2004/02/skos/core#Concept>}  LIMIT 10";
		sparqlQuery += "?sparql-results=json";
		
		StringBuffer urlString = new StringBuffer(API);
		urlString.append("query=").append(sparqlQuery);


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

			//TODO: tot hier
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
