package uk.ac.ids.resources;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.Literal;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.ids.Main;
import uk.ac.ids.data.Namespaces;
import uk.ac.ids.data.Parameters;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.linker.impl.Lexvo;
import uk.ac.ids.util.DataHarvester;
import uk.ac.ids.vocabulary.RDFS;

// http://wiki.restlet.org/docs_2.1/13-restlet/28-restlet/270-restlet/245-restlet.html

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class GenericResource extends ServerResource {
	// Logger instance
	protected static final Logger logger = Logger
			.getLogger(GenericResource.class.getName());

	// Identifier of the resource
	private String resourceID = null;

	// Type (class) of the resource
	private Reference resourceType = null;

	// The name of the data set
	private String datasetName = null;

	// The graph that will contain the data about that resource
	private Graph graph = new Graph();

	// The name of the resource;
	private Reference resource = null;

	// Set of key/value pairs for this resource
	private Map<String, ArrayList<String>> keyValuePairs = new HashMap<String, ArrayList<String>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.UniformResource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		// Get the dataset name from the URI template
		datasetName = (String) getRequest().getAttributes().get("DB");

		// Define the URI for the vocabulary
		Reference vocabNS = new Reference(getRequest().getOriginalRef()
				.getHostIdentifier() + "/" + datasetName + "/vocabulary#");

		// Get the attributes value taken from the URI template
		resourceID = (String) getRequest().getAttributes().get("ID");
		String type = (String) getRequest().getAttributes().get("TYPE");
		resourceType = new Reference(type);
		resourceType.setBaseRef(vocabNS);

		// If no ID has been given, return a 404
		if (resourceID == null || resourceType == null || datasetName == null) {
			setStatus(Status.CLIENT_ERROR_NOT_FOUND);
			setExisting(false);
		}

		// Define the URI for this resource
		resource = new Reference(getRequest().getOriginalRef().toUri());

		// Load the key-values pairs from the JSON API
		try {
			DataHarvester d = new DataHarvester();
			String query = getApplication().getDataSet(datasetName)
					.getPatternFor(resourceType);
			d.setURL(new URL(query.replace("{id}", resourceID)));
			String api_key = Parameters.getInstance().get(Parameters.API_KEY);
			d.setKey(api_key);
			d.setRoot(getApplication().getDataSet(datasetName).getResultRoot(
					resourceType));
			keyValuePairs = d.getKeyValuePairs();
		} catch (Exception e) {
		}

		// Process them
		for (Entry<String, ArrayList<String>> keyValuePair : keyValuePairs
				.entrySet()) {
			// Turn the key into a predicate
			Reference predicate = new Reference(keyValuePair.getKey());

			// Get the range of that predicate
			Reference valueType = getApplication().getDataSet(datasetName)
					.getRangeOf(predicate);

			// See if we need to rewrite the predicate into something else
			Reference otherPredicate = getApplication().getDataSet(datasetName)
					.getReplacementForPredicate(predicate);
			if (otherPredicate != null)
				predicate = otherPredicate;

			// If the predicate is relative, bind it to the vocabulary NS
			if (predicate.isRelative())
				predicate.setBaseRef(vocabNS);

			// Get the values
			ArrayList<String> values = keyValuePair.getValue();

			// See if we need to call a Linker to replace the value
			if (keyValuePair.getKey().equals("#language_name")) {
				LinkerParameters parameters = new LinkerParameters();
				parameters.put(Lexvo.LANG_NAME, values.get(0));
				parameters.put(Lexvo.LANG_NAME_LOCALE, "eng");
				Lexvo lexvo = new Lexvo();
				List<Reference> target = lexvo.getResource(parameters);
				if (target != null) {
					values.clear();
					values.add(target.get(0).toUri().toString());
					valueType = RDFS.RESOURCE;
				}
			}

			for (String value : keyValuePair.getValue()) {
				// Sort of a hack: if theme parent, have the value be preceded
				// by a "C" to get a correct match
				if (keyValuePair.getKey().equals("#cat_parent")) {
					value = "C" + value;
				}
				if (keyValuePair.getKey().equals("#cat_first_parent")) {
					value = "C" + value;
				}

				// If we know the type of this value, use it
				if (valueType != null) {
					// The target value is a Resource
					if (valueType.equals(RDFS.RESOURCE)) {
						Reference object = new Reference(value);
						if (object.isRelative())
							object.setBaseRef(vocabNS);
						if (keyValuePair.getKey().equals("#object_type")) {
							object = new Reference(vocabNS.toString() + type.toString());
						}
						graph.add(resource, predicate, object);
					}

					// The target is an internal link
					else if (getApplication().getDataSet(datasetName)
							.isInternalType(valueType)) {
						logger.info("Internal " + valueType);
						String pattern = "resource/";
						pattern += valueType.toString().substring(1);
						pattern += "/" + value;
						Reference target = new Reference(pattern);
						if (target.isRelative())
							target.setBaseRef(vocabNS);

						graph.add(resource, predicate, target);
					}

					else {
						// Otherwise, add a plain literal
						Literal object = new Literal(value);
						graph.add(resource, predicate, object);
					}
				} else {
					// Otherwise, add a plain literal
					Literal object = new Literal(value);
					graph.add(resource, predicate, object);
				}
			}
		}

		// Look for linking services and apply them
		getApplication().getDataSet(datasetName).applyLinkers(
				getRequest().getOriginalRef().getHostIdentifier(), graph,
				resource, resourceType, keyValuePairs, vocabNS);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#getApplication()
	 */
	@Override
	public Main getApplication() {
		return (Main) super.getApplication();
	}

	/**
	 * Returns an HTML representation of the resource
	 * 
	 * @return an HTML representation of the resource
	 */
	@Get("html")
	public Representation toHTML() {
		Namespaces namespaces = getApplication().getNamespaces();

		// TODO move creation of ids namespace at creation time
		if (!namespaces.isRegistered(datasetName + ":")) {
			Reference ns = new Reference(getRequest().getOriginalRef()
					.getHostIdentifier() + "/" + datasetName + "/vocabulary#");
			namespaces.register(ns.toString(), datasetName + ":");
		}

		// Sort the triples
		List<Link> triples = new ArrayList<Link>();
		for (Link t : graph)
			triples.add(t);
		Collections.sort(triples, new Comparator<Link>() {
			public int compare(Link a, Link b) {
				return a.getTypeRef().toString()
						.compareTo(b.getTypeRef().toString());
			}
		});

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("resource", resource);
		map.put("triples", triples);
		map.put("ns", namespaces);

		return new TemplateRepresentation("resource.html", getApplication()
				.getConfiguration(), map, MediaType.TEXT_HTML);
	}

	/**
	 * Returns an RDF/XML representation of the resource
	 * 
	 * @return an RDF/XML representation of the resource
	 */
	@Get("rdf")
	public Representation toRDFXML() {
		return graph.getRdfXmlRepresentation();
	}

}
