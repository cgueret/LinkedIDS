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

	/** http://example.org#matcher */
	public static final Reference MATCHER;

	/** http://example.org#matcherName */
	public static final Reference MATCHER_NAME;

	/** http://example.org#parameter */
	public static final Reference PARAMETER;

	/** http://example.org#parameterKey */
	public static final Reference PARAMETER_KEY;

	/** http://example.org#parameterValue */
	public static final Reference PARAMETER_VALUE;

	static {
		IGNORED_PROPERTY = new Reference(WRAPPER.NAMESPACE, "IgnoredProperty");
		PATTERN = new Reference(WRAPPER.NAMESPACE, "pattern");
		REPLACE_BY = new Reference(WRAPPER.NAMESPACE, "replaceby");
		MATCHER = new Reference(WRAPPER.NAMESPACE, "matcher");
		MATCHER_NAME = new Reference(WRAPPER.NAMESPACE, "matcherName");
		PARAMETER = new Reference(WRAPPER.NAMESPACE, "parameter");
		PARAMETER_KEY = new Reference(WRAPPER.NAMESPACE, "parameterKey");
		PARAMETER_VALUE = new Reference(WRAPPER.NAMESPACE, "parameterValue");
	}
}
