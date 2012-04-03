package uk.ac.ids.resources;

import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.Link;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.ids.Main;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class VocabularyResource extends ServerResource {
	// The vocabulary graph
	private final Graph vocabulary = new Graph();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.Resource#doInit()
	 */
	protected void doInit() throws ResourceException {
		// Get the reference for the vocabulary namespace
		Reference ns = new Reference(getRequest().getOriginalRef().getHostIdentifier() + "/vocabulary#");

		for (Link triple : getApplication().getMappings()) {
			Reference s = triple.getSourceAsReference().clone();
			if (s.isRelative())
				s.setBaseRef(ns);

			Reference p = triple.getTypeRef().clone();
			if (p.isRelative())
				p.setBaseRef(ns);

			if (triple.hasReferenceTarget()) {
				Reference o = triple.getTargetAsReference().clone();
				if (o.isRelative())
					o.setBaseRef(ns);
				vocabulary.add(new Link(s, p, o));
			} else {
				vocabulary.add(new Link(s, p, triple.getTargetAsLiteral()));
			}
			vocabulary.add(triple);
		}
	}

	/**
	 * Returns an RDF/XML representation of the resource
	 * 
	 * @return an RDF/XML representation of the resource
	 */
	@Get
	public Representation toRDFXML() {
		return vocabulary.getRdfXmlRepresentation();
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
}
