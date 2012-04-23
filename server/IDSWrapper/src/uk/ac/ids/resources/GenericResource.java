package uk.ac.ids.resources;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.ids.Main;
import uk.ac.ids.data.Namespaces;
import uk.ac.ids.data.Parameters;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.linker.impl.DBpedia;
import uk.ac.ids.linker.impl.GeoNames;
import uk.ac.ids.linker.impl.Lexvo;
import uk.ac.ids.vocabulary.OWL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// http://wiki.restlet.org/docs_2.1/13-restlet/28-restlet/270-restlet/245-restlet.html

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
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

	// Set of key/value pairs for this resource
	private Map<String, String> keyValuePairs = new HashMap<String, String>();

	@SuppressWarnings("serial")
	public static final Map<String, String> PLURAL = new HashMap<String, String>() {
		{
			put("country", "countries");
			put("region", "regions");
			put("document", "documents");
			put("theme", "themes");
		}
	};

	private static final Reference RDFS_Resource = new Reference("http://www.w3.org/2000/01/rdf-schema#Resource");

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

		// Load the key-values pairs from the JSON API
		loadKeyValuePairs();

		// Process them
		for (Entry<String, String> keyValuePair : keyValuePairs.entrySet()) {
			// Turn the key into a predicate
			Reference predicate = new Reference(keyValuePair.getKey());

			// Get the range of that predicate
			Reference valueType = getApplication().getMappings().getRangeOf(predicate);

			// See if we need to rewrite the predicate into something else
			Reference otherPredicate = getApplication().getMappings().getReplacementForPredicate(predicate);
			if (otherPredicate != null)
				predicate = otherPredicate;

			// If the predicate is relative, bind it to the vocabulary NS
			Reference vocabNS = new Reference(getRequest().getOriginalRef().getHostIdentifier() + "/vocabulary");
			if (predicate.isRelative())
				predicate.setBaseRef(vocabNS);

			// Get the value
			String value = keyValuePair.getValue();

			// See if we need to call a Linker to replace the value
			if (keyValuePair.getKey().equals("#language_name")) {
				LinkerParameters parameters = new LinkerParameters();
				parameters.put(Lexvo.LANG_NAME, value);
				parameters.put(Lexvo.LANG_NAME_LOCALE, "eng");
				Lexvo lexvo = new Lexvo();
				List<Reference> target = lexvo.getResource(parameters);
				if (target != null) {
					value = target.get(0).toUri().toString();
					valueType = RDFS_Resource;
				}
			}

			// If we know the type of this value, use it
			if (valueType != null) {
				// The target value is a Resource
				if (valueType.equals(RDFS_Resource)) {
					Reference object = new Reference(value);
					if (object.isRelative())
						object.setBaseRef(vocabNS);
					graph.add(resource, predicate, object);
				}

				// The target is an internal link
				else if (getApplication().getMappings().isInternalType(valueType)) {
					String pattern = getApplication().getMappings().getPatternFor(valueType);
					if (pattern != null) {
						Reference object = new Reference(pattern.replace("{id}", value));
						if (object.isRelative())
							object.setBaseRef(vocabNS);
						graph.add(resource, predicate, object);
					}
				}

				// Otherwise, add a plain literal
				else {
					Literal object = new Literal(value);
					graph.add(resource, predicate, object);
				}
			} else {
				// Otherwise, add a plain literal
				Literal object = new Literal(value);
				graph.add(resource, predicate, object);
			}
		}

		// Link to Geonames
		// TODO move that configuration in a ttl file
		if (resourceType.equals("country")) {
			GeoNames b = new GeoNames();
			String countryName = keyValuePairs.get("#country_name");
			String countryCode = keyValuePairs.get("#iso_two_letter_code");
			LinkerParameters params = new LinkerParameters();
			params.put(GeoNames.COUNTRY_CODE, countryCode);
			params.put(GeoNames.COUNTRY_NAME, countryName);
			List<Reference> target = b.getResource(params);
			if (target != null)
				graph.add(resource, OWL.SAME_AS, target.get(0));
		}

		// Link to DBpedia
		// TODO move that configuration in a ttl file
		if (resourceType.equals("theme")) {
			DBpedia b = new DBpedia();
			String themeTitle = keyValuePairs.get("#title");
			LinkerParameters params = new LinkerParameters();
			params.put(DBpedia.THEME_TITLE, themeTitle);
			List<Reference> target = b.getResource(params);
			if (target != null)
				graph.add(resource, OWL.SAME_AS, target.get(0));
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
	 * Load the key values pairs from the JSON API
	 */
	private void loadKeyValuePairs() {
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
			JsonElement e = parser.parse(response.toString());
			if (!e.isJsonObject())
				return;
			JsonElement element = ((JsonObject) e).get("results");
			if (!element.isJsonObject())
				return;
			for (Entry<String, JsonElement> entry : ((JsonObject) element).entrySet()) {
				if (entry.getValue().isJsonObject())
					continue;

				// Store all the entries of the array
				if (entry.getValue().isJsonArray())
					for (JsonElement v : (JsonArray) entry.getValue())
						keyValuePairs.put("#" + entry.getKey(), v.getAsString());

				// Store the single value
				if (entry.getValue().isJsonPrimitive())
					keyValuePairs.put("#" + entry.getKey(), entry.getValue().getAsString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns an HTML representation of the resource
	 * 
	 * @return an HTML representation of the resource
	 */
	@Get("html")
	public Representation toHTML() {
		Namespaces namespaces = getApplication().getNamespaces();

		// TODO move creation of ids namespace at creation time
		if (!namespaces.isRegistered("ids:")) {
			Reference ns = new Reference(getRequest().getOriginalRef().getHostIdentifier() + "/vocabulary#");
			namespaces.register(ns.toString(), "ids:");
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resource", resource);
		map.put("triples", graph);
		map.put("ns", namespaces);

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
