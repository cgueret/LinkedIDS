/**
 * 
 */
package uk.ac.ids.resources;

import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.ids.Main;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class SearchResource extends ServerResource {
	// Requested keyword
	private String searchTerm;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		// Get the attributes value taken from the URI template
		searchTerm = (String) getRequest().getAttributes().get("TERM");
	}

	@Get
	public Representation getResults() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#getApplication()
	 */
	@Override
	public Main getApplication() {
		return (Main) super.getApplication();
	}
}
