package uk.ac.ids.resources;

import org.restlet.ext.rdf.Graph;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import uk.ac.ids.data.Vocabulary;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class VocabularyResource extends ServerResource {
	/**
	 * Returns an RDF/XML representation of the resource
	 * 
	 * @return an RDF/XML representation of the resource
	 */
	@Get
	public Representation toRDFXML() {
		System.out.println(getRequest());
		Vocabulary vocab = Vocabulary.getInstance(getContext(), getRequest().getOriginalRef());
		Graph g = vocab.getGraph();
		return g.getRdfXmlRepresentation();
	}
}
