/**
 * 
 */
package uk.ac.ids.resources;

import java.util.Map;
import java.util.Map.Entry;

import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import org.json.JSONArray;
import org.json.JSONObject;

import uk.ac.ids.Main;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class SearchResource extends ServerResource {
	// Requested keyword
	private String searchTerm;
	private String datasetName;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		// Get the attributes value taken from the URI template
		searchTerm = (String) getRequest().getAttributes().get("TERM");
		datasetName = (String) getRequest().getAttributes().get("DB");

	}

	/**
	 * @return
	 */
	@Get("json")
	public Representation getResults() {
		Map<String, String> results = getApplication().getMappings(datasetName)
				.getMetaData().getSearch().getResults(searchTerm);

		JSONObject json = new JSONObject();
		try {
			JSONArray array = new JSONArray();
			for (Entry<String, String> result : results.entrySet()) {
				JSONObject e = new JSONObject();
				e.put("url", result.getKey());
				e.put("label", result.getValue());
				array.put(e);
			}
			json.put("results", array);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JsonRepresentation(json);
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
}
