package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;


public class WRAPPER {
	/** http://example.org/ */
	public static final String NAMESPACE = "http://example.org/";

	/** http://example.org/IgnoredProperty */
	public final static Reference IGNORED_PROPERTY;

	static {
		IGNORED_PROPERTY = new Reference(WRAPPER.NAMESPACE, "IgnoredProperty");
	}

}
