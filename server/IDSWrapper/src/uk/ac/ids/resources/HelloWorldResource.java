package uk.ac.ids.resources;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

// http://wiki.restlet.org/docs_2.1/13-restlet/275-restlet/252-restlet.html

/**
 * @author cgueret
 * 
 */
public class HelloWorldResource extends ServerResource {
	/**
	 * @return
	 */
	@Get
	public String represent() {
		return "Hello world!";
	}
}
