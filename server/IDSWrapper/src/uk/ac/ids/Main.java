package uk.ac.ids;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.routing.Router;

import uk.ac.ids.resources.ConfigResource;
import uk.ac.ids.resources.GenericResource;
import uk.ac.ids.resources.HelloWorldResource;
import uk.ac.ids.resources.VocabularyResource;

import freemarker.template.Configuration;

/**
 * @author cgueret
 * 
 */
public class Main extends Application {
	protected static final Logger logger = Logger.getLogger(Main.class.getName());

	/** The Freemarker's configuration. */
	private Configuration configuration;

	/**
	 * Creates a root Restlet that will receive all incoming calls and route
	 * them to the corresponding handlers
	 */
	@Override
	public Restlet createInboundRoot() {
		// initialize the Freemarker's configuration
		configuration = new Configuration();
		configuration.setTemplateLoader(new ContextTemplateLoader(getContext(), "war:///templates"));

		// Create the router
		Router router = new Router(getContext());

		// Handler for requests to generic resources
		router.attach("/{DB}/resource/{TYPE}/{ID}", GenericResource.class);

		// Handler for requests to vocabulary resources
		router.attach("/{DB}/vocabulary/{id}", VocabularyResource.class);

		// Handler for requests to parameters setting
		router.attach("/config", ConfigResource.class);

		// Say Hello if any other resource is requested
		router.attachDefault(HelloWorldResource.class);

		// Activate content filtering based on extensions
		getTunnelService().setExtensionsTunnel(true);

		return router;
	}

	/**
	 * Returns the Freemarker's configuration.
	 * 
	 * @return The Freemarker's configuration.
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
}
