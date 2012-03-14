package uk.ac.ids;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.Get;
import org.restlet.routing.Router;

import uk.ac.ids.resources.GenericResource;
import uk.ac.ids.resources.VocabularyResource;

/**
 * @author cgueret
 * 
 */
public class Main extends Application {
	/**
	 * Creates a root Restlet that will receive all incoming calls and route
	 * them to the corresponding handlers
	 */
	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());

		// Handler for requests to generic resources
		router.attach("{db}/resource/{type}/{id}", GenericResource.class);

		// Handler for requests to vocabulary resources
		router.attach("{db}/vocabulary/{id}", VocabularyResource.class);

		// Activate content filtering based on extensions
		getTunnelService().setExtensionsTunnel(true);

		return router;
	}

	/**
	 * @return
	 */
	@Get
	public String represent() {
		return "Hello world!";
	}
}
