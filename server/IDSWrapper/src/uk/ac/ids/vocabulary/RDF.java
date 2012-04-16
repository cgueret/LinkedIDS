/**
 * 
 */
package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class RDF {
	/** http://www.w3.org/1999/02/22-rdf-syntax-ns# */
	public static final String NAMESPACE = "http://www.w3.org/1999/02/22-rdf-syntax-ns";

	/** http://www.w3.org/1999/02/22-rdf-syntax-ns#type */
	public final static Reference TYPE;

	static {
		TYPE = new Reference(RDF.NAMESPACE, "type");
	}
}
