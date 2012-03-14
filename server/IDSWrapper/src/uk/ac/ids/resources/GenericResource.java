package uk.ac.ids.resources;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class GenericResource extends ServerResource {
	private String resourceID = null;
	private String resourceType = null;
	private String dataSource = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.UniformResource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		// Get the attributes value taken from the URI template
		resourceID = (String) getRequest().getAttributes().get("id");
		resourceType = (String) getRequest().getAttributes().get("type");
		dataSource = (String) getRequest().getAttributes().get("db");

		// If no ID has been given, return a 404
		if (resourceID == null || resourceType == null || dataSource == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			setExisting(false);
		}
	}

}
