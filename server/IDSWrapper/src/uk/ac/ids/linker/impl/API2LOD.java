package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.restlet.data.Reference;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;

public class API2LOD extends Linker {
	// Parameters
	public static final String ORGANISATION = "organisation_id";

	@Override
	protected List<Reference> getFromService(LinkerParameters parameters) {
		logger.info(parameters.toKey());
		// Prepare the API call
		String apiCall = parameters.get("queryPattern");
		apiCall = apiCall.replace("{id}", parameters.get("queryPatternID"));
		
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
				JsonArray resultsArray = obj.get(parameters.get("resultsRoot")).getAsJsonArray();
				List<Reference> res = new ArrayList<Reference>();
				for (int i=0; i < resultsArray.size(); i++) {
					JsonObject entry = resultsArray.get(i).getAsJsonObject();
					String result = parameters.get("resultsPattern");
					for (Entry<String, JsonElement> a: entry.entrySet()) 
						if (result.contains(a.getKey()))
							result = result.replace("{" + a.getKey() + "}", a.getValue().getAsString());
					res.add(new Reference(parameters.get("API2LOD") + "/" + result));
				}
				return res;
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
