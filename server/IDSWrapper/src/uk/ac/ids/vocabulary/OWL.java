/**
 * 
 */
package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class OWL {
	/** http://www.w3.org/2002/07/owl# */
	public static final String NAMESPACE = "http://www.w3.org/2002/07/owl";

	/** http://www.w3.org/2002/07/owl#sameAs */
	public final static Reference SAME_AS;

	static {
		SAME_AS = new Reference(OWL.NAMESPACE, "sameAs");
	}

}
