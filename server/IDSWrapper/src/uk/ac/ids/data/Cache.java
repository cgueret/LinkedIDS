package uk.ac.ids.data;

import java.util.Date;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public class Cache {
	// Entity type in the AppEngine data store
	private final static String CACHE_ENTITY = "Cache";

	// Singleton instance
	private static Cache instance = null;

	/**
	 * Constructor
	 */
	protected Cache() {
	}

	/**
	 * Return an instance of Parameters
	 * 
	 * @return the singleton instance of Parameters
	 */
	public static Cache getInstance() {
		if (instance == null)
			instance = new Cache();
		return instance;
	}

	/**
	 * Update or create the parameter and associate the new value
	 * 
	 * @param name
	 *            the name of the cached entry
	 * @param value
	 *            the value to assign
	 */
	public void set(String name, Object value) {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Entity parameter = null;
		try {
			Key k = KeyFactory.createKey(CACHE_ENTITY, name);
			parameter = datastore.get(k);
		} catch (EntityNotFoundException e) {
			parameter = new Entity(CACHE_ENTITY, name);
		}
		parameter.setProperty("value", value);
		parameter.setProperty("date", new Date());
		datastore.put(parameter);
	}

	/**
	 * Return the value of cached entry
	 * 
	 * @param name
	 *            the name of the cached entry
	 * @return the value of the entry
	 * @throws EntityNotFoundException
	 *             if the entry has not been found
	 */
	public Object getValue(String name) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key k = KeyFactory.createKey(CACHE_ENTITY, name);
		Entity parameter = datastore.get(k);
		return parameter.getProperty("value");
	}

	/**
	 * Return the last set date of a cached entry
	 * 
	 * @param name
	 *            the name of the cached entry
	 * @return the last modification date
	 * @throws EntityNotFoundException
	 *             if the entry has not been found
	 */
	public Date getLastModificationDate(String name)
			throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key k = KeyFactory.createKey(CACHE_ENTITY, name);
		Entity parameter = datastore.get(k);
		return (Date) parameter.getProperty("date");
	}
}
