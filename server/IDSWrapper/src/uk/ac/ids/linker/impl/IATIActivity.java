package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.restlet.data.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;

public class IATIActivity extends Linker {
	// Parameters
	public static final String ORGANISATION = "organisation_id";

	@Override
	protected List<Reference> getFromService(LinkerParameters parameters) {
		// Get the ID
		String id = parameters.get(ORGANISATION);
		
		// Prepare the API call
		String apiCall = "http://oipa.openaidsearch.org/api/v2/activities/?format=json&organisations={id}";
		apiCall = apiCall.replace("{id}", id);
		
		try {
			// Compose the URL
			URL url = new URL(apiCall.toString());

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
				JsonArray resultsArray = obj.get("objects").getAsJsonArray();
				for (int i=0; i < resultsArray.size(); i++) {
					JsonObject entry = resultsArray.get(i).getAsJsonObject();
					String iati = entry.get("iati_identifier").getAsString();
					List<Reference> res = new ArrayList<Reference>();
					res.add(new Reference("http://sws.geonames.org/" + iati));
					return res;
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
