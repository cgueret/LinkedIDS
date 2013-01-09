package uk.ac.ids.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.Reference;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.ids.data.Parameters;
import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.linker.impl.DBpedia;
import uk.ac.ids.vocabulary.OWL;

public class IATIExperimenter {
	
		private final static String API = "http://dbpedia.org/sparql?";


		public List<Reference> runexperiment(String themeTitle){
			
		
		
		//String themeTitle = "Food security";
		
		List<Reference> res = new ArrayList<Reference>();

		// Build the sparql query: limit to 5 (could be one)
		String sparqlQuery = "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>";
		sparqlQuery += "prefix skos: <http://www.w3.org/2004/02/skos/core#>";
		sparqlQuery += "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>";
		sparqlQuery += "select distinct ?Concept where  {?Concept rdfs:label \"";
		sparqlQuery += themeTitle;
		sparqlQuery += "\"@en .}  LIMIT 5";

		// ?Concept rdf:type skos:Concept.

		try {
			// Compose the URL
			sparqlQuery = URLEncoder.encode(sparqlQuery.toString(), "utf-8");
			StringBuffer urlString = new StringBuffer(API);
			urlString.append("query=").append(sparqlQuery);
			URL url = new URL(urlString.toString());

			// Issue the request
			StringBuffer response = new StringBuffer();

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);

			}
			reader.close();

			
			// Parse the response: return only the first URI. If there are nu
			// URIs found, return null
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(response.toString()));
			Document doc = db.parse(is);
			NodeList results = doc.getElementsByTagName("td");

			if (results.getLength() > 0) {
				for (int i = 0; i < results.getLength(); i++) {
					
					if (results.item(i).getTextContent() !=null){
						String uri = results.item(i).getTextContent();
					
					res.add(new Reference(uri));
					}

					
				}
				return res;

			} else {
				return res;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return res;
		} catch (IOException e) {
			e.printStackTrace();
			return res;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return res;
		} catch (SAXException e) {
			e.printStackTrace();
			return res;
		}
				}

		
		
		public static void main(String[] args) throws IOException {
        	IATIExperimenter d = new IATIExperimenter();
        	System.out.println("\n\n\nNEW EXPERIMENT\n\n\n");
        	String file_loc = "C:\\Users\\victor\\IDS project\\eldis_themes.xml";
				  
			File file = new File(file_loc);
			
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	         DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
		        Document doc = db.parse(file);
	             Element docEle = doc.getDocumentElement();
	             // Print root element of the document
	             System.out.println("Root element of the document: "+ docEle.getNodeName());

	             NodeList titleList = docEle.getElementsByTagName("title");
	             for (int i = 0; i < titleList.getLength(); i++){
	            	 
	            	String eldis_theme = titleList.item(i).getTextContent();
	     			List<Reference> result = d.runexperiment(eldis_theme);
	     			
	     			System.out.println(eldis_theme + " , " + result.toString());
	     			Thread.sleep(500);
	            	 
	             }
	             
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	         

            
			
			
			
			/**/
		}
}