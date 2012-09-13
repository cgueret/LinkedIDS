/**
 * 
 */
package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class FOAF {
	/** http://xmlns.com/foaf/0.1/ */
	public static final String NAMESPACE = "http://xmlns.com/foaf/0.1/";

	/** http://xmlns.com/foaf/0.1/depiction */
	public final static Reference DEPICTION;

	static {
		DEPICTION = new Reference(FOAF.NAMESPACE, "depiction");
	}
}
