package com.francetelecom.orangetv.junithistory.server.service;

public class AbstractTest {

	public static final String TEST_PATHNAME = "./resources_tests";
	public static final String HTML_DIR_PATHNAME = TEST_PATHNAME + "/html/";
	public static final String XML_DIR_PATHNAME = TEST_PATHNAME + "/files/";
	public static final String XML_FILE_UNIQUE_GROUP_NAME = "Newbox-R4";
	public static final String TARGET_FILENAME = "target.css";

	public static final String XML_PREFIX = "NBXR4-";
	public static final String XML_VERSION = "SDK13.3.0-A";
	public static final String XML_ROOT_NAME = XML_PREFIX + XML_VERSION;
	public static final String XML_EXT = ".xml";
	public static final String XML_FILE = XML_ROOT_NAME + XML_EXT;

	public static final String XML_FILE_TEMP = "NBXR4-SDK12.5-B-INSPECTOR.xml";

	public static final String MINI_FILE = "minifile.xml";

	public static final String LOG_EXT = ".log";
	public static final String LOG_FILE = XML_ROOT_NAME + LOG_EXT;

	public static final String TXT_EXT = ".txt";
	public static final String TXT_FILE = XML_ROOT_NAME + TXT_EXT;

	public static final String HTML_FILE = XML_ROOT_NAME + ".html";

	public static final String XMLPART_PREFIX = "MIB4-";
	public static final String XMLPART_VERSION = "07.00.40A";
	public static final String XMLPART_ROOT_NAME = XMLPART_PREFIX + XMLPART_VERSION;
	public static final String XMLPART_EXT = ".xml.part3";
	public static final String XMLPART_FILE = XMLPART_ROOT_NAME + XMLPART_EXT;

}
