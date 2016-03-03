package com.francetelecom.orangetv.junithistory.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.BeforeClass;
import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.dto.DtoHtmlPage;
import com.francetelecom.orangetv.junithistory.server.dto.DtoListHtmlPages;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;

/**
 * JUnit tests for HtmlBuilderManager
 * 
 * @author ndmz2720
 *
 */
public class HtmlBuilderManagerTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(HtmlBuilderManagerTest.class.getName());
	private static final HtmlBuilderManager htmlManager = HtmlBuilderManager.get();
	private static final ReportManager reportManager = ReportManager.get();

	@BeforeClass
	public static void init() {
		// htmlManager.init(XML_DIR_PATHNAME, "./files/");
	}

	@Test
	public void testGetResourceInputStream() throws Exception {

		String resourceName = "style.css";
		InputStream inputStream = htmlManager.getResourceInputStream(resourceName);
		assertNotNull("inputStream cannot be null", inputStream);

		File target = new File(XML_DIR_PATHNAME, resourceName);
		boolean result = FileUtils.copyInputStream(inputStream, target);
		assertTrue("result cannot be false!", result);

		assertTrue(FileUtils.verifyFile(target, false));
	}

	@Test
	public void testBuildHtmlPageForListTestSuite() throws Exception {
		DbTestSuiteGroup group = new DbTestSuiteGroup("MIB4", "MIB4", "MIB4-");

		DtoTestSuiteInstance dtoTestSuite0 = reportManager.readXmlReport(new File(XML_DIR_PATHNAME, XML_FILE_TEMP));
		assertNotNull("dtoTestSuite1 cannot be null!", dtoTestSuite0);

		DtoTestSuiteInstance dtoTestSuite1 = reportManager.readXmlReport(new File(XML_DIR_PATHNAME, XML_FILE));
		assertNotNull("dtoTestSuite1 cannot be null!", dtoTestSuite1);

		File dir = new File(XML_DIR_PATHNAME);
		String pattern = FileUtils.createRegexXmlPart(XMLPART_ROOT_NAME);
		List<File> xmlFiles = FileUtils.getListFile(dir, pattern);
		assertNotNull("list of xml files cannot be null", xmlFiles);

		DtoTestSuiteInstance dtoTestSuite2 = reportManager.readXmlReport(xmlFiles);
		assertNotNull("dtoTestSuite2 cannot be null!", dtoTestSuite2);

		List<DtoTestSuiteInstance> listDtoDatas = new ArrayList<>();
		listDtoDatas.add(dtoTestSuite0);
		listDtoDatas.add(dtoTestSuite1);
		listDtoDatas.add(dtoTestSuite2);

		String relatifPathForLog = PathManager.get().getUploadRelativePathFromHtmlToLog();
		DtoListHtmlPages result = htmlManager.buildHtmlPageForListTestSuite(group.getName(), listDtoDatas,
				relatifPathForLog);
		assertNotNull("DtoListHtmlPages cannot be null!", result);

		this.writeHtmlResult(HTML_DIR_PATHNAME, result.getMainPage());
		assertNotNull("DtoListHtmlPages.listHtmlPages cannot be null!", result.getListHtmlPages());

		for (DtoHtmlPage htmlPage : result.getListHtmlPages()) {
			this.writeHtmlResult(HTML_DIR_PATHNAME, htmlPage);
		}

	}

	@Test
	public void testBuildHtmPageForXmlReport() throws Exception {

		DtoTestSuiteInstance dtoTestSuiteInstance = reportManager.readXmlReport(new File(XML_DIR_PATHNAME, XML_FILE));
		assertNotNull("dtoTestSuiteInstance cannot be null!", dtoTestSuiteInstance);

		DbTestSuiteInstance testSuite = dtoTestSuiteInstance.getTestSuiteInstance();
		assertNotNull("testSuite cannot be null!", testSuite);

		testSuite.setUser(new DbTestUser("toto"));
		testSuite.setDate(new Date());
		testSuite.setIptvkit("Iptvkit 4.4.25-MIB4-SNAPSHOT");
		testSuite.setFirmware("MIB4 FW 7.0m40");

		StringBuilder comment = new StringBuilder();
		comment.append("Serie1 a serie5 \n");
		comment.append("\n");
		comment.append("ALL TESTS");
		testSuite.setComment(comment.toString());

		String relatifPathForLog = PathManager.get().getUploadRelativePathFromHtmlToLog();
		DtoHtmlPage htmlPage = htmlManager.buildHtmPageFromTestSuite(dtoTestSuiteInstance, relatifPathForLog);

		this.writeHtmlResult(HTML_DIR_PATHNAME, htmlPage);
	}

	@Test
	public void testBuildHtmlPageFromDb() throws Exception {

		DtoTestSuiteInstance dtoDatas = DaoManager.get().loadTestSuite(1);
		String relatifPathForLog = PathManager.get().getUploadRelativePathFromHtmlToLog();
		DtoHtmlPage htmlPage = htmlManager.buildHtmPageFromTestSuite(dtoDatas, relatifPathForLog);

		this.writeHtmlResult(HTML_DIR_PATHNAME, htmlPage);

	}

	// ---------------------------------------------- private methods
	private void writeHtmlResult(String dir, DtoHtmlPage htmlPage) {

		assertNotNull("htmlPage cannot be null!", htmlPage);
		assertNotNull("htmlPage.listLines cannot be null!", htmlPage.getListLines());
		assertNotNull("htmlPage.pageName cannot be null!", htmlPage.getPageName());

		FileUtils.verifyDirectory(new File(dir), true);
		File htmlFile = new File(dir, htmlPage.getPageName());
		if (htmlFile.exists()) {
			htmlFile.delete();
		}
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(htmlFile));

			for (String line : htmlPage.getListLines().getLines()) {
				log.config(line);
				writer.write(line);
				writer.write("\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException ignored) {
				}
			}
		}

	}
}
