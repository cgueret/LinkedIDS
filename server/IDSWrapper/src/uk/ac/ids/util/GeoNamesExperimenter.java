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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import uk.ac.ids.data.Parameters;
import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.linker.impl.DBpedia;
import uk.ac.ids.linker.impl.IATI;
import uk.ac.ids.vocabulary.OWL;

public class GeoNamesExperimenter {
	
	private final static String API = "http://api.geonames.org/search?";


		public List<Reference> runexperiment(String countryName, String countryCode) throws UnsupportedEncodingException{
			
			List<Reference> res = new ArrayList<Reference>();

		
			StringBuffer urlString = new StringBuffer(API);
		

			urlString.append("name_equals=").append(URLEncoder.encode(countryName,"UTF-8"));
			urlString.append("&");
			urlString.append("country=").append(countryCode);
			urlString.append("&");
			urlString.append("username=idswrapper");
			urlString.append("&");
			urlString.append("featureCode=PCLI");
			//urlString.append("&");
			//urlString.append("type=json");

			try {
				// Compose the URL
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

				
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(response.toString()));
				Document doc = db.parse(is);
				NodeList results = doc.getElementsByTagName("geonameId");

				if (results.getLength() > 0) {
					for (int i = 0; i < results.getLength(); i++) {
						
						if (results.item(i).getTextContent() !=null){
							String uri = results.item(i).getTextContent();
						
							res.add(new Reference("http://sws.geonames.org/" + uri));
						}

						
					}
					return res;

				} else {
					return res;
				}

				
				
				
				
				/* Parse the response:JSON
				JsonParser parser = new JsonParser();
				JsonElement results = parser.parse(response.toString());
				if (results.isJsonObject()) {
					JsonObject obj = (JsonObject) results;
					//if (obj.get("totalResultsCount").getAsInt() == 1) {
						JsonArray array = obj.get("geonames").getAsJsonArray();
						JsonObject entry = array.get(0).getAsJsonObject();
						String id = entry.get("geonameId").getAsString();
						List<Reference> res = new ArrayList<Reference>();
						res.add(new Reference("http://sws.geonames.org/" + id));
						return res;
					//}
				}

				return null;
				*/
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
				}

		
		
		public static void main(String[] args) throws IOException {
        	GeoNamesExperimenter d = new GeoNamesExperimenter();
        	System.out.println("\n\n\nNEW EXPERIMENT\n\n\n");
        	
        	
        	String file_loc = "C:\\Users\\victor\\IDS project\\eldis_countries.xml";
				  
			File file = new File(file_loc);
			
			 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	         DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
		        Document doc = db.parse(file);
	             Element docEle = doc.getDocumentElement();
	             // Print root element of the document
	             System.out.println("Root element of the document: "+ docEle.getNodeName());

	             NodeList titleList = docEle.getElementsByTagName("list-item");
	           
	             for (int i = 0; i < titleList.getLength(); i++){
	            	 
	            	String c_name = titleList.item(i).getChildNodes().item(4).getTextContent();
	            	String c_code = titleList.item(i).getChildNodes().item(2).getTextContent();

	     			List<Reference> result = d.runexperiment(c_name, c_code);
	     			
	     			System.out.print(c_name + " ; " + c_code + " ; ");
	     			if(result !=null){
	     				System.out.println(result.toString());
	     			}
	     			else{System.out.println( "NOCOUNTRY");}
	     			
	     			Thread.sleep(700);
	            	 
	             }
	             
	             
	             
			} catch (ParserConfigurationException e) {
				//e.printStackTrace();
				System.out.println( "ERROR");
			} catch (SAXException e) {
				//e.printStackTrace();
				System.out.println( "ERROR");
			} catch (InterruptedException e) {
				//e.printStackTrace();
				System.out.println( "ERROR");
			}
	         

            
			
			
			
			/**/
		}
}