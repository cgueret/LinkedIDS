package uk.ac.ids.vocabulary;

import org.restlet.data.Reference;

public class WRAPPER {
	/** http://api2lod.appspot.com/vocabulary# */
	public static final String NAMESPACE = "http://api2lod.appspot.com/vocabulary";

	/** http://api2lod.appspot.com/vocabulary#IgnoredProperty */
	public static final Reference IGNORED_PROPERTY;

	/** http://api2lod.appspot.com/vocabulary#pattern */
	public static final Reference PATTERN;

	/** http://api2lod.appspot.com/vocabulary#replaceby */
	public static final Reference REPLACE_BY;

	/** http://api2lod.appspot.com/vocabulary#matcher */
	public static final Reference MATCHER;

	/** http://api2lod.appspot.com/vocabulary#matcherName */
	public static final Reference MATCHER_NAME;

	/** http://api2lod.appspot.com/vocabulary#parameter */
	public static final Reference PARAMETER;

	/** http://api2lod.appspot.com/vocabulary#parameterKey */
	public static final Reference PARAMETER_KEY;

	/** http://api2lod.appspot.com/vocabulary#parameterValue */
	public static final Reference PARAMETER_VALUE;

	public static final Reference APIURL;

	public static final Reference EXAMPLE;

	public static final Reference LINK;

	static {
		IGNORED_PROPERTY = new Reference(WRAPPER.NAMESPACE, "IgnoredProperty");
		PATTERN = new Reference(WRAPPER.NAMESPACE, "pattern");
		REPLACE_BY = new Reference(WRAPPER.NAMESPACE, "replaceby");
		MATCHER = new Reference(WRAPPER.NAMESPACE, "matcher");
		MATCHER_NAME = new Reference(WRAPPER.NAMESPACE, "matcherName");
		PARAMETER = new Reference(WRAPPER.NAMESPACE, "parameter");
		PARAMETER_KEY = new Reference(WRAPPER.NAMESPACE, "parameterKey");
		PARAMETER_VALUE = new Reference(WRAPPER.NAMESPACE, "parameterValue");
		APIURL = new Reference(WRAPPER.NAMESPACE, "api_url");
		EXAMPLE = new Reference(WRAPPER.NAMESPACE, "example");
		LINK = new Reference(WRAPPER.NAMESPACE, "link");
	}
}
