package uk.ac.ids.linker;

import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.Reference;


import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public abstract class Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(Linker.class.getName());

	// Entity type in the AppEngine data store
	private final static String DS_ENTITY = "ExternalResource";

	// Entity type in the AppEngine data store
	private final static String RESOURCE_PROPERTY = "Resource";

	/**
	 * @param countryName
	 * @param countryCode
	 * @return
	 */
	public Reference getResource(LinkerParameters parameters) {
		logger.info("Get " + parameters);
		try {
			// Try to return the result from the cache
			return getFromCache(parameters);
		} catch (EntityNotFoundException e) {
			// Try to get it from geoname
			Reference uri = getFromService(parameters);

			// If successful, save it
			if (uri != null)
				saveToCache(parameters, uri);

			return uri;
		}

	}

	/**
	 * @param parameters
	 * @return
	 */
	protected abstract Reference getFromService(LinkerParameters parameters);

	/**
	 * @param parameters
	 * @return
	 * @throws EntityNotFoundException
	 */
	private Reference getFromCache(LinkerParameters parameters) throws EntityNotFoundException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		Query q = new Query(DS_ENTITY);
		for (Entry<String, String> filter : parameters.entrySet())
			q.addFilter(filter.getKey(), FilterOperator.EQUAL, filter.getValue());
		PreparedQuery pq = datastore.prepare(q);
		Entity entity = pq.asSingleEntity();

		if (entity == null)
			throw new EntityNotFoundException(null);

		return new Reference((String) entity.getProperty(RESOURCE_PROPERTY));
	}

	/**
	 * @param parameters
	 * @param uri
	 */
	private void saveToCache(LinkerParameters parameters, Reference uri) {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Entity entity = new Entity(DS_ENTITY);
		for (Entry<String, String> prop : parameters.entrySet())
			entity.setProperty(prop.getKey(), prop.getValue());
		
		entity.setProperty(RESOURCE_PROPERTY, uri.toString());
		
		datastore.put(entity);
	}

}
