/**
 * 
 */
package uk.ac.ids.linker.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

import org.restlet.data.Reference;

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
		String result = LexvoIdentifiersAPI.getTermURI(parameters.get(LANG_NAME), parameters.get(LANG_NAME_LOCALE));

		// Look for the reference to the iso639-3 resource
		try {

			// Load the RDF
			StringBuffer response = new StringBuffer();
			URL url = new URL(result);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("Accept", "application/rdf+xml");
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();

			// FIXME the RDF from Lexvo is broken at the moment
			// Graph graph = new Graph();
			// GraphBuilder builder = new GraphBuilder(graph);
			// RdfXmlReader r = new RdfXmlReader(new
			// StringRepresentation(response), builder);
			// r.parse();
		} catch (Exception e) {
		}

		return new Reference(result);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinkerParameters parameters = new LinkerParameters();
		parameters.put(Lexvo.LANG_NAME, "English");
		parameters.put(Lexvo.LANG_NAME_LOCALE, "eng");
		Lexvo l = new Lexvo();
		logger.info(l.getFromService(parameters).toString());
	}
}
