package uk.ac.ids.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ReferenceList;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.vocabulary.RDF;
import uk.ac.ids.vocabulary.RDFS;
import uk.ac.ids.vocabulary.WRAPPER;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class DataSet implements Iterable<Link> {
	// Logger
	protected static final Logger logger = Logger.getLogger(DataSet.class
			.getName());

	// The context in which the mappings are used
	private final Context context;

	// The name of the data set
	private final String datasetName;

	// The metadata for this data set
	private final DataSetMetadata metadata;

	// The RDF graph that stores all the mappings
	private Graph graph = new Graph();

	/**
	 * 
	 */
	public DataSet(Context context, String datasetName, String datasetDirectory) {
		// Save a pointer to the context
		this.context = context;

		// Save the dataSet name
		this.datasetName = datasetName;

		// The metadata for this data set
		this.metadata = new DataSetMetadata(this.datasetName);

		// Get the internal client
		Restlet client = context.getClientDispatcher();

		// Get a list of the files in the mappings directory
		List<Reference> files = new ArrayList<Reference>();
		try {
			Response response = client.handle(new Request(Method.GET,
					datasetDirectory + "/mappings"));
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
				RdfTurtleReader reader = new RdfTurtleReader(
						response.getEntity(), builder);
				reader.parse();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Load the metadata
		logger.info("Load " + datasetDirectory + "/metadata.ttl");
		Response response = client.handle(new Request(Method.GET,
				datasetDirectory + "/metadata.ttl"));
		try {
			metadata.load(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param predicate
	 * @return
	 */
	public Reference getRangeOf(Reference predicate) {
		for (Link l : graph) {
			if (l.getSource().equals(predicate)) {
				if (l.getTypeRef().equals(RDFS.RANGE)) {
					return l.getTargetAsReference();
				}
			}
		}
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
				if (l.getTypeRef().equals(WRAPPER.REPLACE_BY))
					return l.getTargetAsReference();

		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	public String getPatternFor(final Reference type) {
		for (Link l : graph) {
			if (l.getSource().toString().equals("#" + type)) {
				if (l.getTypeRef().equals(WRAPPER.PATTERN)) {
					return new String(l.getTargetAsLiteral().getValue());
				}
			}
		}
		return null;
	}

	/**
	 * @param type
	 * @return
	 */
	public String getResultRoot(final Reference type) {
		for (Link l : graph) {
			if (l.getSource().toString().equals("#" + type)) {
				if (l.getTypeRef().equals(WRAPPER.RESULT_ROOT)) {
					return new String(l.getTargetAsLiteral().getValue());
				}
			}
		}
		return null;
	}

	/**
	 * @param valueType
	 * @return
	 */
	public boolean isInternalType(Reference type) {
		for (Link l : graph)
			if (l.getSource().equals(type))
				if (l.getTypeRef().equals(RDF.TYPE))
					if (l.getTargetAsReference().equals(RDFS.CLASS))
						return true;
		return false;
	}

	/**
	 * @param targetGraph
	 * @param resource
	 * @param resourceType
	 * @param keyValuePairs
	 */
	public void applyLinkers(String hostIdentifier, Graph targetGraph,
			Reference resource, Reference resourceType,
			Map<String, ArrayList<String>> keyValuePairs, Reference ns) {

		// First, find the matchers
		List<Reference> linkers = new ArrayList<Reference>();
		for (Link l : graph) {
			if (l.getTypeRef().equals(WRAPPER.MATCHER)
					&& l.getSource().toString().equals("#" + resourceType)) {
				Reference matcherBNode = l.getTargetAsReference();
				linkers.add(matcherBNode);
			}
		}

		// Go through the matchers
		for (Reference matcher : linkers) {
			// Get the BNodes for the parameters
			List<Reference> paramNodes = new ArrayList<Reference>();
			for (Link l : graph) {
				if (l.getSource().equals(matcher)
						&& l.getTypeRef().equals(WRAPPER.PARAMETER))
					paramNodes.add(l.getTargetAsReference());
			}
			
			// Find the parameters
			LinkerParameters params = new LinkerParameters();
			for (Reference paramNode : paramNodes) {
				String paramKey = null;
				String paramValueKey = null;
				for (Link l : graph) {
					if (l.getSource().equals(paramNode)) {
						if (l.getTypeRef().equals(WRAPPER.PARAMETER_KEY))
							paramKey = l.getTargetAsLiteral().getValue();
						
						if (l.getTypeRef().equals(WRAPPER.PARAMETER_VALUE))
							paramValueKey = l.getTargetAsLiteral().getValue();
					}
				}
				if (paramValueKey.startsWith("#")) {
					ArrayList<String> values = keyValuePairs
							.get(paramValueKey);
					if (values != null)
						params.put(paramKey, values.get(0));
				} else {
					params.put(paramKey, paramValueKey);
				}
			}
			
			// Find the linker class
			String linkerClass = null;
			for (Link l : graph)
				if (l.getSource().equals(matcher)
						&& l.getTypeRef().equals(WRAPPER.MATCHER_NAME))
					linkerClass = l.getTargetAsLiteral().getValue();

			// Find the linker predicate
			Reference linkerPredicate = null;
			for (Link l : graph)
				if (l.getSource().equals(matcher)
						&& l.getTypeRef().equals(RDF.PREDICATE))
					linkerPredicate = l.getTargetAsReference();
			System.out.println("Predicate " + linkerPredicate.toString());

			// Get the results
			try {
				// Instanciate the linker
				logger.info("Found linker for " + resourceType + " | "
						+ linkerClass);
				Linker linker = (Linker) Class.forName(linkerClass)
						.newInstance();

				// Add the host identifier to the parameters
				params.put("API2LOD", hostIdentifier);
				// Add the linker name too
				params.put("LINKER", linkerClass);

				if (linkerPredicate.isRelative())
					linkerPredicate.setBaseRef(ns);
				if (linker != null) {
					for (Reference ref : linker.getResource(params)) {
						if (ref.isRelative()) {
							logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
									+ ref);
							ref = new Reference(ns + "/" + ref);
						}
						targetGraph.add(resource, linkerPredicate, ref);
					}
				}
			} catch (Exception e) {
				logger.warning("Exception in DataSet");
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return
	 */
	public DataSetMetadata getMetaData() {
		return metadata;
	}
}
