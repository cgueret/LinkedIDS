package uk.ac.ids.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

// http://wiki.restlet.org/docs_2.1/13-restlet/28-restlet/270-restlet/245-restlet.html

public class GenericResource extends ServerResource {
	protected static final Logger logger = Logger.getLogger(GenericResource.class.getName());

	private String resourceID = null;
	private String resourceType = null;
	private String dataSource = null;

	@SuppressWarnings("serial")
	static final Map<String, String> PLURAL = new HashMap<String, String>() {
		{
			put("country", "countries");
		}
	};

	@SuppressWarnings("serial")
	static final Map<String, Reference> MAP = new HashMap<String, Reference>() {
		{
			put("object_type", new Reference("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"));
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.UniformResource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		// Get the attributes value taken from the URI template
		resourceID = (String) getRequest().getAttributes().get("ID");
		resourceType = (String) getRequest().getAttributes().get("TYPE");
		dataSource = (String) getRequest().getAttributes().get("DB");

		// If no ID has been given, return a 404
		if (resourceID == null || resourceType == null || dataSource == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			setExisting(false);
		}

	}

	/**
	 * @return
	 * @throws IOException
	 * @throws EntityNotFoundException
	 */
	@Get
	public Representation represent() throws IOException, EntityNotFoundException {
		// Compose the URL
		StringBuffer urlString = new StringBuffer("http://api.ids.ac.uk/openapi/");
		urlString.append(dataSource).append("/");
		urlString.append("get/").append(PLURAL.get(resourceType)).append("/");
		urlString.append(resourceID).append("/full");
		URL url = new URL(urlString.toString());

		// Get the API key
		// TODO: Fix this ugly reference to statics in ConfigResource
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(ConfigResource.PARAM_ENTITY, ConfigResource.KEY_NAME);
		String api_key = (String) datastore.get(k).getProperty("value");

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
		Graph graph = new Graph();
		JsonParser parser = new JsonParser();
		JsonObject results = (JsonObject) ((JsonObject) parser.parse(response.toString())).get("results");
		Reference resource = new Reference(getRequest().getOriginalRef().toUri());
		Reference ns = new Reference(getRequest().getOriginalRef().getHostIdentifier() + "/vocabulary/");
		for (Entry<String, JsonElement> entry : results.entrySet()) {
			// By default, use the internal vocabulary. Replace with a mapped
			// value when applicable
			Reference predicate = new Reference(ns, resourceType + "." + entry.getKey());
			if (MAP.containsKey(entry.getKey()))
				predicate = MAP.get(entry.getKey());

			JsonElement value = entry.getValue();
			if (value.isJsonPrimitive()) {

				// In the specific case of an object type, change it
				if (entry.getKey().equals("object_type")) {
					Reference object = new Reference(ns, entry.getValue().getAsString());
					graph.add(resource, predicate, object);
				} else {
					// Interpret the literal
					Literal object = null;
					if (((JsonPrimitive) value).isNumber()) // TODO data types
						object = new Literal(entry.getValue().getAsString());
					if (((JsonPrimitive) value).isString())
						object = new Literal(entry.getValue().getAsString());
					graph.add(resource, predicate, object);
				}
			}
		}

		return graph.getRdfXmlRepresentation();
	}

}
