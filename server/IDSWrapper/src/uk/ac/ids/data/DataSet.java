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

	// The RDF graph that stores all the mappings
	private Graph graph = new Graph();

	// The metadata for this data set
	private final DataSetMetadata metadata = new DataSetMetadata();

	/**
	 * 
	 */

	public DataSet(Context context, String datasetDirectory) {
		// Save a pointer to the context
		this.context = context;

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
	public String getPatternFor(Reference type) {
		for (Link l : graph) {
			if (l.getSource().toString().equals("#"+type)) {
				if (l.getTypeRef().equals(WRAPPER.PATTERN)) {
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
	public void applyLinkers(Graph targetGraph, Reference resource,
			Reference resourceType, Map<String, ArrayList<String>> keyValuePairs) {
		for (Link l : graph) {
			if (l.getTypeRef().equals(WRAPPER.MATCHER) && l.getSource().toString().equals("#"+resourceType)) {
				Reference matcherBNode = l.getTargetAsReference();
				logger.info("Found matcher " + matcherBNode);
				String linkerClass = null;
				Reference linkerPredicate = null;
				LinkerParameters params = new LinkerParameters();
				for (Link l2 : graph) {
					if (l2.getSource().equals(matcherBNode)) {
						if (l2.getTypeRef().equals(WRAPPER.MATCHER_NAME))
							linkerClass = l2.getTargetAsLiteral().getValue();
						if (l2.getTypeRef().equals(RDF.PREDICATE))
							linkerPredicate = l2.getTargetAsReference();
						if (l2.getTypeRef().equals(WRAPPER.PARAMETER)) {
							Reference parameterBNode = l2
									.getTargetAsReference();
							String paramKey = null;
							String paramValueKey = null;
							for (Link l3 : graph) {
								if (l3.getSource().equals(parameterBNode)) {
									if (l3.getTypeRef().equals(
											WRAPPER.PARAMETER_KEY))
										paramKey = l3.getTargetAsLiteral()
												.getValue();
									if (l3.getTypeRef().equals(
											WRAPPER.PARAMETER_VALUE))
										paramValueKey = l3.getTargetAsLiteral()
												.getValue();
								}
							}
							logger.info(paramValueKey + " " + keyValuePairs);
							params.put(paramKey,
									keyValuePairs.get(paramValueKey).get(0));
						}
					}
				}

				try {
					Linker linker = (Linker) Class.forName(linkerClass)
							.newInstance();
					for (Reference ref : linker.getResource(params))
						targetGraph.add(resource, linkerPredicate, ref);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
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
