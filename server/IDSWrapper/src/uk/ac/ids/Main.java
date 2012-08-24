package uk.ac.ids;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.routing.Router;

import uk.ac.ids.data.Constants;
import uk.ac.ids.data.DataSet;
import uk.ac.ids.data.Namespaces;
import uk.ac.ids.resources.ClassResource;
import uk.ac.ids.resources.ConfigResource;
import uk.ac.ids.resources.GenericResource;
import uk.ac.ids.resources.DataSetResource;
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

	// The mappings are the configuration to turn the API results into RDF
	// The hashmap is used to associate one such mapping file to each data set
	// served
	private Map<String, DataSet> mappings = new HashMap<String, DataSet>();

	// Namespaces used in the mappings
	private Namespaces namespaces;

	/**
	 * Creates a root Restlet that will receive all incoming calls and route
	 * them to the corresponding handlers
	 */
	@Override
	public Restlet createInboundRoot() {
		// Initialise Freemarker's configuration
		configuration = new Configuration();
		configuration.setTemplateLoader(new ContextTemplateLoader(getContext(), Constants.TEMPLATES_DIR));

		// Initialise the namespaces
		namespaces = new Namespaces();

		// Create the router
		Router router = new Router(getContext());

		// Handler for requests to generic resources
		router.attach("/{DB}/resource/{TYPE}/{ID}", GenericResource.class);

		// Handler for the home page of a data set
		router.attach("/{DB}/resource/{TYPE}", ClassResource.class);

		// Handler for requests to vocabulary terms
		router.attach("/{DB}/term/{TERM}", VocabularyResource.class);

		// Handler for the home page of a data set
		router.attach("/{DB}", DataSetResource.class);

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
	 * Return the mappings object associated to a given data set
	 * 
	 * @return the mappings
	 * 
	 */
	public DataSet getMappings(String datasetName) {
		// If this is the first time we ask for this data set, load its
		// configuration
		if (!mappings.containsKey(datasetName)) {
			DataSet mapping = new DataSet(getContext(), Constants.MAPPINGS_DIR + datasetName + "/");
			mappings.put(datasetName, mapping);
		}

		return mappings.get(datasetName);
	}

	/**
	 * @return the namespaces
	 */
	public Namespaces getNamespaces() {
		return namespaces;
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// Create the HTTP server and listen on port 8182
		// Server server = new Server(Protocol.HTTP, 8182);
		// Application a = new Main();
		// a.setContext(server.getContext());
		// a.start();
		// System.out.println(a.getContext());

		Component component = new Component();
		component.getClients().add(Protocol.WAR);
		component.getServers().add(Protocol.HTTP, 8080);
		component.getDefaultHost().attach(new Main());
		component.start();

	}

}
