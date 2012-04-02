package uk.ac.ids.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.restlet.Client;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class Mappings implements Iterable<Link> {
	// Logger
	protected static final Logger logger = Logger.getLogger(Mappings.class.getName());

	// Range predicate
	private static final Reference RDFS_RANGE = new Reference("http://www.w3.org/2000/01/rdf-schema#range");

	// Replace by predicate
	private static final Reference REPLACE_BY = new Reference("http://example.org/replaceby");

	// The graph that will contain all the mappings data
	private final Graph graph = new Graph();

	// The context in which the mappings are used
	private final Context context;

	/**
	 * 
	 */

	public Mappings(Context context, String mappingsDir) {
		// Save a pointer to the context
		this.context = context;

		// Get the internal client
		Client client = context.getClientDispatcher();

		// Get a list of the files in the mappings directory
		List<Reference> files = new ArrayList<Reference>();
		try {
			Response response = client.handle(new Request(Method.GET, mappingsDir));
			if (response != null)
				for (Reference entry : new ReferenceList(response.getEntity()))
					files.add(entry);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Load the content of all the files into the graph
		for (Reference file : files) {
			logger.info("Load " + file.toString());
			Response response = client.handle(new Request(Method.GET, file));
			try {
				GraphBuilder builder = new GraphBuilder(graph);
				RdfTurtleReader reader = new RdfTurtleReader(response.getEntity(), builder);
				reader.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param predicate
	 * @return
	 */
	public Reference getPredicateRange(Reference predicate) {
		for (Link l : graph)
			if (l.getSource().equals(predicate))
				if (l.getTypeRef().equals(RDFS_RANGE))
					return l.getTargetAsReference();

		return null;
	}

	/**
	 * @return the context
	 */
	public Context getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<Link> iterator() {
		return graph.iterator();
	}

	/**
	 * @param predicate
	 * @return
	 */
	public Reference getReplacementForPredicate(Reference predicate) {
		for (Link l : graph)
			if (l.getSource().equals(predicate))
				if (l.getTypeRef().equals(REPLACE_BY))
					return l.getTargetAsReference();

		return null;
	}
}
