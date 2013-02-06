package uk.ac.ids.linker.impl;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.restlet.data.Reference;

import uk.ac.ids.linker.Linker;
import uk.ac.ids.linker.LinkerParameters;
import uk.ac.ids.util.DataHarvester;

/**
 * @author Christophe Gueret <christophe.gueret@gmail.com>
 * @author Victor de Boer <v.de.boer@vu.nl>
 * 
 */
public class IATISector extends Linker {
	// Logger instance
	protected static final Logger logger = Logger.getLogger(IATISector.class
			.getName());

	// Parameters
	public static final String TITLE = "title";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.ids.linker.Linker#getFromService(uk.ac.ids.linker.LinkerParameters)
	 */
	@Override
	protected List<Reference> getFromService(LinkerParameters parameters) {
		List<Reference> res = new ArrayList<Reference>();

		// Get the parameter
		if (!parameters.containsKey(TITLE))
			return res;
		String title = parameters.get(TITLE);

		try {
			// Get the list of sectors from OIPA
			Map<String, String> codeMap = getCodeMap();

			// Set the first letter to upper case
			StringBuilder sb = new StringBuilder(title);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			title = sb.toString();

			// Return the code
			if (codeMap.containsKey(title)) {
				String root = parameters.get("API2LOD");
				Reference r = new Reference(root + "/oipa/resource/Sector/"
						+ codeMap.get(title));
				res.add(r);
				return res;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return res;
		}
		
		return res;
	}

	/**
	 * @return
	 */
	private Map<String, String> getCodeMap() {
		Map<String, String> codeMap = new HashMap<String, String>();
		try {
			String apiCall = "http://oipa.openaidsearch.org/api/v2/sectors/?format=json&offset=0&limit=5000";
			DataHarvester harvester = new DataHarvester();
			harvester.setURL(new URL(apiCall));
			harvester.setRoot("objects");
			Map<String, ArrayList<String>> keyVal = harvester
					.getKeyValuePairs();
			for (Entry<String, ArrayList<String>> e : keyVal.entrySet()) {
				String k = e.getKey();
				String key = k.substring(k.length() - 4, k.length());
				if (key.equals("code")) {
					ArrayList<String> l = keyVal.get(k.substring(0,
							k.length() - 4) + "name");
					if (l.size() == 1)
						codeMap.put(
								l.get(0).replace('\n', ' ')
										.replaceAll("\\s+", " "), e.getValue()
										.get(0));
				}
			}
		} catch (Exception e) {

		}
		return codeMap;
	}

	public static void main(String[] args) throws IOException {
		IATISector i = new IATISector();
		LinkerParameters parameters = new LinkerParameters();
		parameters.put(TITLE, "Higher education");
		System.out.println(i.getFromService(parameters));
	}
}
