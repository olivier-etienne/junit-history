package com.francetelecom.orangetv.junithistory.server.manager;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public interface IManager {

	public DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DecimalFormat TIME_SEC_FORMAT = new DecimalFormat("#0.000");

	public static final String KEY_SESSION_GWTUPLOADED_FILE = "gwtuploadedfile";
	public static final String KEY_SESSION_GWTUPLOADED_DIR = "gwtuploadeddir";

	public static final String KEY_SESSION_EVENT_START_NEW_UPLOAD = "eventStartNewUpload";
	public static final String KEY_SESSION_EVENT_UPLOAD_ENDED = "eventUploadEnded";

	public static final String HTML_EXT = ".html";
	public static final String TXT_EXT = ".txt";

	public static final String REG_END = "$";
	public static final String REG_ROOT = "([\\w\\.\\-]*)";
	public static final String REG_XML_PART = "(\\.xml\\.part\\d{1,2})";
	public static final String REG_XML = "(\\.xml)";
	public static final String REG_TXT = "(\\.txt)";
	public static final String REG_LOG = "(\\.log)";

	// regex pour extraire la version du rootname d'un fichier
	public static final String REG_VERSION_FROM_ROOTNAME = "([\\.\\d\\w-]*)";

	public static final String REG_XML_XMLPART_LOG_TXT = "(" + REG_XML + "|" + REG_XML_PART + "|" + REG_LOG + "|"
			+ REG_TXT + ")";

	// (plusieurs fichiers xml pour une meme suite de test)
	// fichier *.xml.part1, *.xml.part2, *.xml.part3 pour une agrégation
	public static final String REG_ROOT_XMLPART = REG_ROOT + REG_XML_PART + REG_END;
	public static final Pattern PATTERN_ROOT_XMLPART = Pattern.compile(REG_ROOT_XMLPART);

	// fichier *.xml
	public static final String REG_ROOT_XML = REG_ROOT + REG_XML;
	public static final Pattern PATTERN_ROOT_XML = Pattern.compile(REG_ROOT_XML);

	// fichier *.xml || *.xml.part<n>
	public static final Pattern PATTERN_ROOT_XML_AND_XMLPART = Pattern.compile(REG_ROOT + "(" + REG_XML + "|"
			+ REG_XML_PART + ")" + REG_END);

	// fichier *.xml || *.xml.part<n> || *.txt || *.log
	public static final String REG_ROOT_XML_XMLPART_LOG_TXT = REG_ROOT + REG_XML_XMLPART_LOG_TXT + REG_END;
	public static final Pattern PATTERN_ROOT_XML_XMLPART_LOG_TXT = Pattern.compile(REG_ROOT_XML_XMLPART_LOG_TXT);

	public static String TEST_INIT_NAME = "testInit";
	public static final String PACKAGE_NAME = "com.francetelecom.orangetv.gwt.stb.test.";
	public static final int PACKAGE_TO_CUT_LENGTH = PACKAGE_NAME.length();

	public static final String KEY_SESSION_USER_PROFIL = "UserProfile";

}
