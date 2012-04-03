package uk.ac.ids.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class Parameters {
	// Entity type in the AppEngine data store
	private final static String PARAMETER_ENTITY = "Parameter";

	// Singleton instance
	private static Parameters instance = null;

	/** Parameter for the API_KEY to query the API from IDS */
	public final static String API_KEY = "API_KEY";

	/**
	 * Update or create the parameter and associate the new value
	 * 
	 * @param name
	 *            the name of the parameter
	 * @param value
	 *            the value to assign
	 */
	public void set(String name, String value) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity parameter = null;
		try {
			Key k = KeyFactory.createKey(PARAMETER_ENTITY, name);
			parameter = datastore.get(k);
		} catch (EntityNotFoundException e) {
			parameter = new Entity(PARAMETER_ENTITY, name);
		}
		parameter.setProperty("value", value);
		datastore.put(parameter);
	}

	/**
	 * Return the value of a parameter
	 * 
	 * @param name
	 *            the name of the parameter
	 * @return the value of the parameter
	 * @throws EntityNotFoundException
	 *             if the parameter has not been set
	 */
	public String get(String name) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key k = KeyFactory.createKey(PARAMETER_ENTITY, name);
		Entity parameter = datastore.get(k);
		return (String) parameter.getProperty("value");
	}

	/**
	 * Constructor
	 */
	protected Parameters() {
	}

	/**
	 * Return an instance of Parameters
	 * 
	 * @return the singleton instance of Parameters
	 */
	public static Parameters getInstance() {
		if (instance == null)
			instance = new Parameters();
		return instance;
	}
}
