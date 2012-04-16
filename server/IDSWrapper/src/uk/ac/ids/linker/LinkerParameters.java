package uk.ac.ids.linker;

import java.util.HashMap;
import java.util.TreeSet;

public class LinkerParameters extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5006131847151277706L;

	/**
	 * @return
	 */
	public String toKey() {
		StringBuffer key = new StringBuffer();
		key.append('[');
		for (String k : new TreeSet<String>(keySet()))
			key.append(k).append('=').append(get(k)).append(',');
		key.deleteCharAt(key.length() - 1);
		key.append(']');
		return key.toString();
	}
}
