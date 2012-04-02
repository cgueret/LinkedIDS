package uk.ac.ids;

import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.routing.Router;

import uk.ac.ids.data.Constants;
import uk.ac.ids.data.Mappings;
import uk.ac.ids.data.Namespaces;
import uk.ac.ids.resources.ConfigResource;
import uk.ac.ids.resources.GenericResource;
import uk.ac.ids.resources.VocabularyResource;

import freemarker.template.Configuration;

//http://wiki.restlet.org/docs_2.1/13-restlet/275-restlet/252-restlet.html

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class Main extends Application {
	// Logger
	protected static final Logger logger = Logger.getLogger(Main.class.getName());

	// Freemarker's configuration
	private Configuration configuration;

	// Mappings to turn ReST results into RDF properties
	private Mappings mappings;

	// Namespaces used in the mappings
	private Namespaces namespaces;

	/**
	 * Creates a root Restlet that will receive all incoming calls and route
	 * them to the corresponding handlers
	 */
	@Override
	public Restlet createInboundRoot() {
		// Initialize Freemarker's configuration
		configuration = new Configuration();
		configuration.setTemplateLoader(new ContextTemplateLoader(getContext(), Constants.TEMPLATES_DIR));

		// Initialize the mappings
		mappings = new Mappings(getContext(), Constants.MAPPINGS_DIR);

		// Initialize the namespaces
		namespaces = new Namespaces();

		// Create the router
		Router router = new Router(getContext());

		// Handler for requests to generic resources
		router.attach("/{DB}/resource/{TYPE}/{ID}", GenericResource.class);

		// Handler for requests to vocabulary resources
		router.attach("/vocabulary", VocabularyResource.class);

		// Handler for requests to parameters setting
		router.attach("/config", ConfigResource.class);

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

	/**
	 * @return the mappings
	 */
	public Mappings getMappings() {
		return mappings;
	}

	/**
	 * @return the namespaces
	 */
	public Namespaces getNamespaces() {
		return namespaces;
	}
}
