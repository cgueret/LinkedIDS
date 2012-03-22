package uk.ac.ids.resources;

import org.restlet.data.Form;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import uk.ac.ids.data.Parameters;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class ConfigResource extends ServerResource {

	/**
	 * Set the value of a parameter
	 * 
	 * @return a StringRepresentation that acknowledges the update of the
	 *         parameter
	 */
	@Get
	public Representation setParameter() {
		Form form = this.getQuery();

		// Get the value for the key
		String value = form.getFirstValue("key", true);
		if (value == null)
			return new StringRepresentation("Use config?key=XXXXX to set the API key");

		// Update or create the key if we got a value for it
		Parameters.getInstance().set(Parameters.API_KEY, value);

		return new StringRepresentation("Key set to " + value + " !");
	}
}
