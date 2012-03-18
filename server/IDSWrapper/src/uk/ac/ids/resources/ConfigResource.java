package uk.ac.ids.resources;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class ConfigResource extends ServerResource {
	public final static String PARAM_ENTITY = "Parameter";
	public final static String KEY_NAME = "API_KEY";

	/**
	 * @return
	 */
	@Get
	public Representation setParameter() {
		Form form = this.getQuery();

		// Get the value for the key
		String value = form.getFirstValue("key", true);
		if (value == null)
			return new StringRepresentation("Use config?key=XXXXX to set the API key");

		// Update or create the key if we got a value for it
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity api_key = null;
		try {
			Key k = KeyFactory.createKey(PARAM_ENTITY, KEY_NAME);
			api_key = datastore.get(k);
		} catch (EntityNotFoundException e) {
			api_key = new Entity(PARAM_ENTITY, KEY_NAME);
		}
		api_key.setProperty("value", value);
		datastore.put(api_key);

		return new StringRepresentation("Key set to " + value + " !");
	}
}
