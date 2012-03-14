package uk.ac.ids.resources;

import java.util.logging.Logger;

import org.restlet.data.Status;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

public class GenericResource extends ServerResource {
	protected static final Logger logger = Logger.getLogger(GenericResource.class.getName());

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
		resourceID = (String) getRequest().getAttributes().get("ID");
		resourceType = (String) getRequest().getAttributes().get("TYPE");
		dataSource = (String) getRequest().getAttributes().get("DB");

		// If no ID has been given, return a 404
		if (resourceID == null || resourceType == null || dataSource == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			setExisting(false);
		}
	}

	/**
	 * @return
	 */
	@Get
	public String represent() {
		return "You requested the resource: " + dataSource + "/" + resourceType + "/" + resourceID;
	}

}
