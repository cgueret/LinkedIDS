/**
 * 
 */
package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class RDFS {
	/** http://www.w3.org/2000/01/rdf-schema# */
	public static final String NAMESPACE = "http://www.w3.org/2000/01/rdf-schema";

	/** http://www.w3.org/2000/01/rdf-schema#Class */
	public final static Reference CLASS;

	/** http://www.w3.org/2000/01/rdf-schema#range */
	public final static Reference RANGE;

	static {
		CLASS = new Reference(RDFS.NAMESPACE, "Class");
		RANGE = new Reference(RDFS.NAMESPACE, "range");
	}

}
