package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

public class WRAPPER {
	/** http://example.org# */
	public static final String NAMESPACE = "http://example.org";

	/** http://example.org#IgnoredProperty */
	public static final Reference IGNORED_PROPERTY;

	/** http://example.org#pattern */
	public static final Reference PATTERN;

	/** http://example.org#replaceby */
	public static final Reference REPLACE_BY;

	static {
		IGNORED_PROPERTY = new Reference(WRAPPER.NAMESPACE, "IgnoredProperty");
		PATTERN = new Reference(WRAPPER.NAMESPACE, "pattern");
		REPLACE_BY = new Reference(WRAPPER.NAMESPACE, "replaceby");
	}

}
