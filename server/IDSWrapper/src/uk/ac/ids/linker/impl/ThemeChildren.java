package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.data.Reference;

import uk.ac.ids.data.Parameters;
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
public class ThemeChildren extends Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(ThemeChildren.class.getName());

	// Parameters
	public static final String CHILDREN_URL = "children_url";

	// The host identifier
	private final String hostIdentifier;

	/**
	 * @param hostIdentifier
	 */
	public ThemeChildren(String hostIdentifier) {
		this.hostIdentifier = hostIdentifier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected List<Reference> getFromService(LinkerParameters parameters) {
		if (!parameters.containsKey(CHILDREN_URL))
			return null;
		String children_url = parameters.get(CHILDREN_URL);

		List<Reference> res = new ArrayList<Reference>();

		try {
			URL url = new URL(children_url);
			// Get the API key
			String api_key = Parameters.getInstance().get(Parameters.API_KEY);

			// Issue the API request
			StringBuffer response = new StringBuffer();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Token-Guid", api_key);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// Parse the response
			JsonParser parser = new JsonParser();
			JsonElement e = parser.parse(response.toString());

			JsonElement results = ((JsonObject) e).get("results");
			if (results.isJsonArray()) {
				for (JsonElement elt : (JsonArray) results) {
					if (elt.isJsonObject()) {
						JsonObject obj = (JsonObject) elt;
						String obj_id = obj.get("object_id").getAsString().toString();
						if (obj_id.length() > 0) {
							Reference child = new Reference(hostIdentifier + "/eldis/resource/theme/" + obj_id);
							res.add(child);
						}
					}
				}
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return res;

	}

}
