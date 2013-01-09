package uk.ac.ids.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import org.restlet.ext.rdf.Graph;
import org.restlet.ext.rdf.GraphBuilder;
import org.restlet.ext.rdf.Link;
import org.restlet.ext.rdf.internal.xml.RdfXmlReader;
import org.restlet.representation.StringRepresentation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.linker.impl.Lexvo;
import uk.ac.ids.linker.impl.LexvoIdentifiersAPI;


public class LexvoExperimenter {
	
		public List<Reference> runexperiment(String eldis_lang) {
			
		//	List<Reference> res = new ArrayList<Reference>();
			
			LinkerParameters parameters = new LinkerParameters();
			parameters.put(Lexvo.LANG_NAME, eldis_lang);
			parameters.put(Lexvo.LANG_NAME_LOCALE, "eng");
			Lexvo l = new Lexvo();
			return l.getFromService(parameters);
			
			//logger.info(l.getFromService(parameters).toString());
			
				}

		
		public static void main(String[] args) throws IOException {
        	LexvoExperimenter d = new LexvoExperimenter();
        	System.out.println("\n\n\nNEW LEXVO EXPERIMENT\n\n\n");
        	String file_loc = "C:\\Users\\victor\\IDS project\\Languages.xml";
				  
			File file = new File(file_loc);
			
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	         DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
		        Document doc = db.parse(file);
	            Element docEle = doc.getDocumentElement();
	             // Print root element of the document
	             System.out.println("Root element of the document: "+ docEle.getNodeName());

	             NodeList titleList = docEle.getElementsByTagName("item");
	             for (int i = 0; i < titleList.getLength(); i++){
	            	 
	            	String eldis_lang = titleList.item(i).getTextContent();
	     			List<Reference> result = d.runexperiment(eldis_lang);
	     			
	     			System.out.println(eldis_lang + " , " + result.toString());
	     			Thread.sleep(500);
	            	 
	             }
	             
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	         

            
			

		}
}