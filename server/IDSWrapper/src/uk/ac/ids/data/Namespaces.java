/**
 * 
 */
package uk.ac.ids.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.Reference;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * 
 */
public class Namespaces {
	protected static final Logger logger = Logger.getLogger(Namespaces.class.getName());

	/** List of namespaces */
	// TODO load namespaces from external file
	public static final Map<String, String> NS = new HashMap<String, String>() {
		// Serial
		private static final long serialVersionUID = 6448355116508480221L;
		{
			put("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "rdf:");
			put("http://www.w3.org/2002/07/owl#", "owl:");
			put("http://dbpedia.org/ontology/", "dbpedia-owl:");
		}
	};

	/**
	 * Turn a full reference into a CURIE using the namespace table
	 * 
	 * @param reference
	 * @return the CURIE representation of the reference
	 */
	public String prettify(Reference reference) {
		String label = reference.toUrl().toString();
		for (Entry<String, String> ns : NS.entrySet()) {
			String[] s = label.split(ns.getKey());
			if (s.length == 2)
				return ns.getValue() + s[1];
		}
		return label;
	}

	/**
	 * Register a new name space
	 * 
	 * @param namespace
	 * @param name
	 */
	public void register(String namespace, String name) {
		NS.put(namespace, name);
	}

	/**
	 * @param namespace
	 *            a namespace
	 * @return true if the namespace is already registered
	 */
	public boolean isRegistered(String namespace) {
		return NS.containsKey(namespace);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Namespaces p = new Namespaces();
		logger.info(p.prettify(new Reference("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")));
		logger.info(p.prettify(new Reference("http://www.w3.org/2002/07/owl#sameAs")));
		logger.info(p.prettify(new Reference("http://dbpedia.org/ontology/PopulatedPlace/areaMetro")));
		logger.info(p.prettify(new Reference("http://idswrapper.appspot.com/vocabulary#site")));
		p.register("http://idswrapper.appspot.com/vocabulary#", "ids:");
		logger.info(p.prettify(new Reference("http://idswrapper.appspot.com/vocabulary#site")));
	}

}
