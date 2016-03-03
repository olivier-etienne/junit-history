package com.francetelecom.orangetv.junithistory.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

public class DaoManagerTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoManagerTest.class.getName());
	private static final DaoManager manager = DaoManager.get();

	@Test
	public void testSaveSuiteTest() throws Exception {

		DtoTestSuiteInstance dtoDatas = ReportManager.get().readXmlReport(new File(XML_DIR_PATHNAME, XML_FILE));
		assertNotNull("dtoDatas cannot be null!", dtoDatas);

		DbTestSuiteInstance testSuiteInstance = dtoDatas.getTestSuiteInstance();
		assertNotNull("suite cannot be null!", testSuiteInstance);

		testSuiteInstance.setDate(new Date());
		testSuiteInstance.setFirmware("7.9.8");
		testSuiteInstance.setIptvkit("IPTVKIT XXX");
		testSuiteInstance.setUser(new DbTestUser("toto"));
		testSuiteInstance.setComment("Ceci est un commentaire");

		boolean result = manager.saveTestSuite(dtoDatas, "token");
		assertTrue("saveTestSuite() must return true!", result);
		assertTrue("suite.id must be defined!", testSuiteInstance.getId() != IDbEntry.ID_UNDEFINED);

		// suppression
		// int suiteId = testSuiteInstance.getId();
		// result = manager.deleteTestSuite(suiteId, "token");
		// assertTrue("deleteTestSuite() must return true!", result);

	}

	@Test
	public void testLoadTestSuite() throws Exception {

		DtoTestSuiteInstance dtoDatas = manager.loadTestSuite(1);
		assertNotNull("dtoDatas cannot be null!", dtoDatas);

		DbTestSuiteInstance suite = dtoDatas.getTestSuiteInstance();
		assertNotNull("suite cannot be null!", suite);

		List<DbTestInstance> listTests = dtoDatas.getListDbTestInstances();
		assertNotNull("listTests cannot be null!", suite);

		for (DbTestInstance test : listTests) {
			log.info(test.toString());
		}

		List<DbStatsCategoryInstance> listStats = dtoDatas.getListDbStatsCategoryInstances();
		assertNotNull("listStats cannot be null!", listStats);

		for (DbStatsCategoryInstance dbStatsCategoryInstance : listStats) {
			assertNotNull("statistics cannot be null!", dbStatsCategoryInstance.getTestStatistics());
			log.info(dbStatsCategoryInstance.getTestStatistics().toString());
		}
	}
}
