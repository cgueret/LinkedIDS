package uk.ac.ids.experimenters;

import java.io.File;
import java.io.IOException;

import org.restlet.data.MediaType;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;
import org.restlet.representation.FileRepresentation;
import org.restlet.representation.Representation;

public class Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// File file = new File("src/uk/ac/ids/util/test.ttl");
		File file = new File("war/WEB-INF/mappings/country.ttl");
		Representation representation = new FileRepresentation(file, MediaType.APPLICATION_RDF_TURTLE);
		Graph graph = new Graph();
		GraphBuilder builder = new GraphBuilder(graph);
		RdfTurtleReader reader = new RdfTurtleReader(representation, builder);
		reader.parse();
		for (Link l : graph)
			System.out.println(l);
	}
}
