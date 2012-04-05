/**
 * 
 */
package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.logging.Logger;

import org.restlet.data.Reference;
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.xml.RdfXmlReader;
import org.restlet.representation.StringRepresentation;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class Lexvo extends Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(Lexvo.class.getName());

	// Parameters
	public static final String LANG_NAME = "langName";
	public static final String LANG_NAME_LOCALE = "langNameLocale";

	// The prefix for the ISO639-3 code
	protected static final String ISO639_3_PREFIX = "http://lexvo.org/id/iso639-3/";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected Reference getFromService(LinkerParameters parameters) {
		// Get the main page of the language
		String targetStr = LexvoIdentifiersAPI.getTermURI(parameters.get(LANG_NAME), parameters.get(LANG_NAME_LOCALE));
		Reference target = new Reference(targetStr);

		// Look for the reference to the iso639-3 resource
		try {

			// Load the RDF
			StringBuffer response = new StringBuffer();
			HttpURLConnection connection = (HttpURLConnection) target.toUrl().openConnection();
			connection.setRequestProperty("Accept", "application/rdf+xml");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			Graph graph = new Graph();
			GraphBuilder builder = new GraphBuilder(graph);
			RdfXmlReader r = new RdfXmlReader(new StringRepresentation(response), builder);
			r.parse();

			// Look for the desired value
			for (Link stmt : graph)
				if (stmt.getSourceAsReference().equals(new Reference(target)))
					if (stmt.getTypeRef().equals(new Reference("http://lexvo.org/ontology#means")))
						if (stmt.getTargetAsReference().toString().startsWith(ISO639_3_PREFIX))
							target = stmt.getTargetAsReference();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return target;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		LinkerParameters parameters = new LinkerParameters();
		parameters.put(Lexvo.LANG_NAME, "Spanish");
		parameters.put(Lexvo.LANG_NAME_LOCALE, "eng");
		Lexvo l = new Lexvo();
		logger.info(l.getFromService(parameters).toString());
	}
}
