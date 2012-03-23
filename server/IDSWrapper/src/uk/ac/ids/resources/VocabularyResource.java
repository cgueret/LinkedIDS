package uk.ac.ids.resources;

import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.GraphHandler;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 *
 */
public class VocabularyResource extends ServerResource {

	// The name of the resource;
	private Reference resource = null;
	
	// The graph that will contain the data about that resource
	private Graph vocGraph = new Graph();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.resource.UniformResource#doInit()
	 */
	@Override
	protected void doInit() throws ResourceException {
		try {
			loadVocabulary();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Define the URI for this resource
		resource = new Reference(getRequest().getOriginalRef().toUri());
		
		
		

	}

	
	private void loadVocabulary() throws Exception {
		String myVocPath = "war:///linkedids-schema.ttl";
		Representation myRep = new FileRepresentation(myVocPath, MediaType.APPLICATION_RDF_TURTLE);
		RdfTurtleReader myReader = new RdfTurtleReader(myRep, new GraphBuilder(vocGraph));
		myReader.parse();
		

	}
}
