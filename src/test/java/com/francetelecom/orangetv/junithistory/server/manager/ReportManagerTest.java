package com.francetelecom.orangetv.junithistory.server.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;

/**
 * JUnitTest for ReportManager
 * 
 * @author ndmz2720
 *
 */
public class ReportManagerTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(ReportManagerTest.class.getName());
	private static final ReportManager manager = ReportManager.get();

	@Test(expected = JUnitHistoryException.class)
	public void testReadXmlReportNotExist() throws Exception {

		manager.readXmlReport(new File("toto"));

	}

	@Test
	public void testBuildListDtoSuiteInstanceFromArchive() throws Exception {

		String archivePath = "C:/Users/ndmz2720/Documents/temp/rapport_history";
		Map<DtoTestSuiteInstance, List<File>> result = ReportManager.get().buildListDtoSuiteInstanceFromArchive(
				archivePath);
		assertNotNull("result cannot be null!", result);
		assertTrue("result cannot be empty!", !result.isEmpty());
	}

	@Test(expected = JUnitHistoryException.class)
	public void testReadXmlReport_WrongFile() throws Exception {

		manager.readXmlReport(new File(XML_DIR_PATHNAME, LOG_FILE));
	}

	@Test
	public void testReadXmlReport() throws Exception {

		DtoTestSuiteInstance result = manager.readXmlReport(new File(XML_DIR_PATHNAME, XML_FILE));
		assertNotNull("result cannot be null!", result);

		DbTestSuiteInstance testSuiteInstance = result.getTestSuiteInstance();
		this.assertAndLog(testSuiteInstance);

		DbTestSuiteGroup testSuiteGroup = testSuiteInstance.getTestSuiteGroup();
		assertNotNull("testSuiteGroup cannot be null", testSuiteGroup);
		assertEquals("Wrong value for group.name!", XML_FILE_UNIQUE_GROUP_NAME, testSuiteGroup.getName());

		List<DbTestInstance> listTestInstances = result.getListDbTestInstances();
		this.assertAndLog(listTestInstances);

		List<DbStatsCategoryInstance> listStats = result.getListDbStatsCategoryInstances();
		this.assertAndLogStats(listStats, 2);

	}

	@Test
	public void testReadXmlReport_Serie() throws Exception {

		File dir = new File(XML_DIR_PATHNAME);
		String pattern = FileUtils.createRegexXmlPart(XMLPART_ROOT_NAME);
		List<File> xmlFiles = FileUtils.getListFile(dir, pattern);
		assertNotNull("list of xml files cannot be null", xmlFiles);

		DtoTestSuiteInstance result = manager.readXmlReport(xmlFiles);

		DbTestSuiteInstance testSuiteInstance = result.getTestSuiteInstance();
		this.assertAndLog(testSuiteInstance);

		List<DbTestInstance> listTestInstances = result.getListDbTestInstances();
		this.assertAndLog(listTestInstances);

		List<DbStatsCategoryInstance> listStats = result.getListDbStatsCategoryInstances();
		this.assertAndLogStats(listStats, 2);
	}

	// ------------------------------------------------------ private methods
	private void assertAndLog(DbTestSuiteInstance testSuiteInstance) {
		assertNotNull("testSuiteInstance cannot be null!", testSuiteInstance);
		log.config("TestSuiteInstance: " + testSuiteInstance.toString());
	}

	private void assertAndLog(List<DbTestInstance> listTestInstances) {
		assertNotNull("listTestInstances cannot be null!", listTestInstances);
		assertTrue("listTestInstances cannot be empty!", !listTestInstances.isEmpty());

		log.config("");
		log.config("LIST OF TESTS");
		for (DbTestInstance dbTestInstance : listTestInstances) {
			log.config(dbTestInstance.toString());
			DbTestMessage testMessage = dbTestInstance.getMessage();
			if (testMessage != null) {
				this.logListLines(testMessage.getMessage(), " >> ");
				this.logListLines(testMessage.getStackTrace(), " >> stack: ");
				this.logListLines(testMessage.getOutputLog(), " >> log: ");
			}

			assertNotNull("TestClass cannot be null for test: " + dbTestInstance.getName(), dbTestInstance.gettClass());
		}

	}

	private void logListLines(String text, String prefix) {

		List<String> lines = ObjectUtils.createListLines(text);
		for (String line : lines) {
			log.config(prefix + line);
		}

	}

	private void assertAndLogStats(List<DbStatsCategoryInstance> listStats, int minItems) {

		assertNotNull("listStats cannot be null!", listStats);
		assertTrue("listStats cannot be empty!", !listStats.isEmpty());
		assertTrue("listStats must have at least " + minItems + " items", listStats.size() > minItems);

		log.config("");
		log.config("LIST OF STATS");
		for (DbStatsCategoryInstance stats : listStats) {
			log.config(stats.toString());
		}

	}

}
