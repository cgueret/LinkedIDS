/**
 * 
 */
package uk.ac.ids.data;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.turtle.RdfTurtleReader;
import org.restlet.representation.Representation;

import uk.ac.ids.vocabulary.RDFS;
import uk.ac.ids.vocabulary.WRAPPER;

/**
 * @author Christophe Guéret <christophe.gueret@gmail.com>
 * 
 */
public class DataSetMetadata {
	private String label = "";
	private String description = "";
	private String apiURL = "";
	private String depiction = "";
	private Map<String, String> examples = new HashMap<String, String>();

	/**
	 * @param entity
	 * @throws IOException
	 */
	public void load(Representation entity) throws IOException {
		Graph graph = new Graph();
		GraphBuilder builder = new GraphBuilder(graph);
		RdfTurtleReader reader = new RdfTurtleReader(entity, builder);
		reader.parse();

		for (Link l : graph) {
			if (l.getSourceAsReference().toString().equals("#api2lod_metadata")) {
				// The label
				if (l.getTypeRef().equals(RDFS.LABEL))
					label = l.getTargetAsLiteral().getValue();

				// Description of the data set
				if (l.getTypeRef().equals(RDFS.COMMENT))
					description = l.getTargetAsLiteral().getValue();

				// The URL of the wrapped API
				if (l.getTypeRef().equals(WRAPPER.APIURL))
					apiURL = l.getTargetAsLiteral().getValue();

				// The image associated to the data set
				System.out.println(l.getTypeRef());
				if (l.getTypeRef().toString().equals("http://xmlns.com/foaf/0.1/depiction")) {
					System.out.println(l);

					depiction = l.getTargetAsLiteral().getValue();
				}
			}

			// An example resource
			if (l.getTypeRef().equals(WRAPPER.EXAMPLE)) {
				Reference example = l.getTargetAsReference();
				String exampleTarget = "";
				String exampleLabel = "";
				for (Link l2 : graph) {
					if (l2.getSourceAsReference().equals(example)) {
						if (l2.getTypeRef().equals(WRAPPER.LINK))
							exampleTarget = l2.getTargetAsLiteral().getValue();
						if (l2.getTypeRef().equals(RDFS.LABEL))
							exampleLabel = l2.getTargetAsLiteral().getValue();
					}
				}
				examples.put(exampleLabel, exampleTarget);
			}
		}
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the apiURL
	 */
	public String getApiURL() {
		return apiURL;
	}

	/**
	 * @return the depiction
	 */
	public String getDepiction() {
		return depiction;
	}

	/**
	 * @return the examples
	 */
	public Collection<Entry<String, String>> getExamples() {
		return examples.entrySet();
	}

}
