package com.francetelecom.orangetv.junithistory.server.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

public class FileUtilsTest extends AbstractTest {

	private static final String DECIMAL_PATTERN = "#0.000";
	private static final DecimalFormat DEC_FORMAT = new DecimalFormat(DECIMAL_PATTERN);

	@Test
	public void testNumberFormat() {

		long ms = 25457;
		double value = new Double(ms) / 1000;
		BigDecimal timeInSec = new BigDecimal(value);

		System.out.println(DEC_FORMAT.format(new Double(ms) / 1000));

	}

	@Test
	public void testDeleteDirectoryAndAllContent() {

		File dir = new File(
				"C:\\Users\\ndmz2720\\Documents\\RA\\divers\\GwtJUnitHistory\\war\\upload\\7o33pyzbuls81a58nl53x7y9t");
		assertTrue("deleteDirectoryAndAllContent must return true!", FileUtils.deleteDirectoryAndAllContent(dir));
	}

	@Test
	public void testGetExtFromFile() {

		// extension of *.log
		String ext = FileUtils.getExtFromFile(new File(LOG_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", LOG_EXT, ext);

		// extension of *.txt
		ext = FileUtils.getExtFromFile(new File(TXT_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", TXT_EXT, ext);

		// ===================== XML FILE =================================
		// extension of *.xml
		ext = FileUtils.getExtFromFile(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", XML_EXT, ext);

		// extension of *.xml
		ext = FileUtils.getExtFromFile(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML_AND_XMLPART);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", XML_EXT, ext);

		// extension of *.xml
		ext = FileUtils.getExtFromFile(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", XML_EXT, ext);
		// ================================================================

		// =============== XML.PART FILE ===============================
		// extension of *.xml.partx
		ext = FileUtils.getExtFromFile(new File(XMLPART_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", XMLPART_EXT, ext);

		// extension of *.xml.partx
		ext = FileUtils.getExtFromFile(new File(XMLPART_FILE), FileUtils.PATTERN_ROOT_XMLPART);
		assertNotNull("extension cannot be null!", ext);
		assertEquals("Wrong value for extension!", XMLPART_EXT, ext);

		// ==================================================================

	}

	@Test
	public void testGetVersionFromRootName() {

		String rootname = XMLPART_ROOT_NAME;
		String version = FileUtils.getVersionFromRootName(XMLPART_PREFIX, rootname);
		assertEquals("wrong version for xml/part file!", XMLPART_VERSION, version);

		rootname = XML_ROOT_NAME;
		version = FileUtils.getVersionFromRootName(XML_PREFIX, rootname);
		assertEquals("wrong version for xml file!", XML_VERSION, version);
	}

	@Test
	public void testGetFileNameNoExt() {

		// rootname of *.log
		String root = FileUtils.getFileNameNoExt(new File(LOG_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XML_ROOT_NAME, root);

		// rootname of *.txt
		root = FileUtils.getFileNameNoExt(new File(TXT_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XML_ROOT_NAME, root);

		// ===================== XML FILE =================================
		// rootname of *.xml
		root = FileUtils.getFileNameNoExt(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XML_ROOT_NAME, root);

		// rootname of *.xml
		root = FileUtils.getFileNameNoExt(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML_AND_XMLPART);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XML_ROOT_NAME, root);
		// rootname of *.xml
		root = FileUtils.getFileNameNoExt(new File(XML_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XML_ROOT_NAME, root);
		// ================================================================

		// =============== XML.PART FILE ===============================
		// rootname of *.xml.partx
		root = FileUtils.getFileNameNoExt(new File(XMLPART_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XMLPART_ROOT_NAME, root);

		// rootname of *.xml.partx
		root = FileUtils.getFileNameNoExt(new File(XMLPART_FILE), FileUtils.PATTERN_ROOT_XMLPART);
		assertNotNull("root name cannot be null!", root);
		assertEquals("Wrong value for root name!", XMLPART_ROOT_NAME, root);

		// ==================================================================

		// ==================== HTML FILE ==========================
		// rootname of *.html doesn't match!
		root = FileUtils.getFileNameNoExt(new File(HTML_FILE), FileUtils.PATTERN_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("root name cannot be null!", root);
		assertNotSame("Wrong value for root name!", XML_ROOT_NAME, root);
		// =========================================================

	}

	@Test
	public void testgetListFileWithPattern() {

		File dir = new File(XML_DIR_PATHNAME);
		List<File> xmlFiles = FileUtils.getListFile(dir, FileUtils.REG_ROOT_XML);
		assertNotNull("list of xml files cannot be null", xmlFiles);
		assertEquals("Wrong size for list of xml files", 2, xmlFiles.size());

		List<File> xmlPartFiles = FileUtils.getListFile(dir, FileUtils.REG_ROOT_XMLPART);
		assertNotNull("list of xml.partx files cannot be null", xmlPartFiles);
		assertEquals("Wrong size for list of xml files", 5, xmlPartFiles.size());

		List<File> xmlPartXmlLogTxtFiles = FileUtils.getListFile(dir, FileUtils.REG_ROOT_XML_XMLPART_LOG_TXT);
		assertNotNull("list of xml.partx files cannot be null", xmlPartXmlLogTxtFiles);
		assertEquals("Wrong size for list of xml files", 10, xmlPartXmlLogTxtFiles.size());

		// pattern avec rootname variable
		String pattern = FileUtils.createRegexXml(XML_ROOT_NAME);
		xmlFiles = FileUtils.getListFile(dir, pattern);
		assertNotNull("list of xml files cannot be null", xmlFiles);
		assertEquals("Wrong size for list of xml files", 1, xmlFiles.size());

		pattern = FileUtils.createRegexXmlPart(XMLPART_ROOT_NAME);
		xmlPartFiles = FileUtils.getListFile(dir, pattern);
		assertNotNull("list of xml.partx files cannot be null", xmlPartFiles);
		assertEquals("Wrong size for list of xml files", 5, xmlPartFiles.size());

		pattern = FileUtils.createRegexXmlPart(XML_ROOT_NAME);
		xmlFiles = FileUtils.getListFile(dir, pattern);
		assertNotNull("list of xml files cannot be null", xmlFiles);
		assertEquals("Wrong size for list of xml files", 0, xmlFiles.size());

	}

	@Test
	public void testVerifySameRootnames() {

		File dir = new File(XML_DIR_PATHNAME);
		List<File> xmlPartFiles = FileUtils.getListFile(dir, FileUtils.REG_ROOT_XMLPART);
		assertNotNull("list of xml.partx files cannot be null", xmlPartFiles);
		assertTrue("the list of xml.part files must have the same root name!",
				FileUtils.verifySameRootnames(xmlPartFiles));

	}

}
