package uk.ac.ids.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.ids.Main;
import uk.ac.ids.data.Parameters;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

// http://wiki.restlet.org/docs_2.1/13-restlet/28-restlet/270-restlet/245-restlet.html

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 *
 */
public class GenericResource extends ServerResource {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(GenericResource.class.getName());

	// Identifier of the resource
	private String resourceID = null;

	// Type (class) of the resource
	private String resourceType = null;

	// The data source (eldis / bridge)
	private String dataSource = null;

	// The graph that will contain the data about that resource
	private Graph graph = new Graph();

	// The name of the resource;
	private Reference resource = null;

	@SuppressWarnings("serial")
	public static final Map<String, String> PLURAL = new HashMap<String, String>() {
		{
			put("country", "countries");
		}
	};

	@SuppressWarnings("serial")
	public static final Map<String, Reference> MAP = new HashMap<String, Reference>() {
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

		// Define the URI for this resource
		resource = new Reference(getRequest().getOriginalRef().toUri());

		try {
			// Compose the URL
			StringBuffer urlString = new StringBuffer("http://api.ids.ac.uk/openapi/");
			urlString.append(dataSource).append("/");
			urlString.append("get/").append(PLURAL.get(resourceType)).append("/");
			urlString.append(resourceID).append("/full");
			URL url = new URL(urlString.toString());

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
			JsonObject results = (JsonObject) ((JsonObject) parser.parse(response.toString())).get("results");
			Reference ns = new Reference(getRequest().getOriginalRef().getHostIdentifier() + "/vocabulary/");
			for (Entry<String, JsonElement> entry : results.entrySet()) {
				// By default, use the internal vocabulary. Replace with a
				// mapped
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
						if (((JsonPrimitive) value).isNumber()) // TODO data
																// types
							object = new Literal(entry.getValue().getAsString());
						if (((JsonPrimitive) value).isString())
							object = new Literal(entry.getValue().getAsString());
						graph.add(resource, predicate, object);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#getApplication()
	 */
	@Override
	public Main getApplication() {
		return (Main) super.getApplication();
	}

	/**
	 * Returns an HTML representation of the resource
	 * 
	 * @return an HTML representation of the resource
	 */
	@Get("html")
	public Representation toHTML() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resource", resource);

		Set<Link> triples = new HashSet<Link>();
		Iterator<Link> it = graph.iterator();
		while (it.hasNext()) {
			Link link = it.next();
			triples.add(link);
		}
		map.put("triples", triples);

		return new TemplateRepresentation("resource.html", getApplication().getConfiguration(), map,
				MediaType.TEXT_HTML);

	}

	/**
	 * Returns an RDF/XML representation of the resource
	 * 
	 * @return an RDF/XML representation of the resource
	 */
	@Get("rdf")
	public Representation toRDFXML() {
		return graph.getRdfXmlRepresentation();
	}

}
