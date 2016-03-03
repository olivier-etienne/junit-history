package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.server.util.TestStatistics;

public class DaoStatsCategoryInstanceTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoStatsCategoryInstanceTest.class.getName());

	private final static DaoStatsCategoryInstance dao = DaoStatsCategoryInstance.get();

	@Test
	public void testListStats() throws Exception {

		List<DbStatsCategoryInstance> result = dao.listStats();
		assertNotNull("list of DbStatsCategoryInstance cannot be null!", result);
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbStatsCategoryInstance result = dao.getById(9999);
		assertNull("DbStatsCategoryInstance must be null!", result);
	}

	@Test
	public void testDeleteStats_notExist() throws Exception {

		boolean result = dao.deleteStats(9999);
		assertFalse("deleteStats() must be false!", result);
	}

	@Test
	public void testDeleteStatsForSuite() throws Exception {

		int suiteId = 8;
		int count = DaoStatsCategoryInstance.get().countForSuite(suiteId);

		if (count > 0) {
			log.info(count + " stats to delete...");

			boolean result = DaoStatsCategoryInstance.get().deleteStatsForTestSuite(suiteId);
			assertTrue("deleteStatsForTestSuite() must be true!", result);

			count = DaoStatsCategoryInstance.get().countForSuite(suiteId);
			assertEquals("all stats must have been deleted!", 0, count);
		} else {
			log.warning("no  to delete!");

		}
	}

	@Test
	public void testCreateStats() throws Exception {

		String token = "tutu";
		DaoStatsCategoryInstance daoWithTransaction = DaoStatsCategoryInstance.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);
			DbTestClassCategory category = DaoTestClassCategory.get().getById(2, true);

			DbStatsCategoryInstance stats = new DbStatsCategoryInstance(new LazyTestSuiteInstance(4), category);

			TestStatistics tStats = this.buildTestStatistics(33);
			stats.setTestStatistics(tStats);

			boolean result = daoWithTransaction.createStats(stats);
			assertTrue("stats.id", stats.getId() != IDbEntry.ID_UNDEFINED);
			this.assertGetById(stats, daoWithTransaction);

			// list for suite
			List<DbStatsCategoryInstance> list = daoWithTransaction.listStatsForSuite(4);
			assertNotNull("list of stats cannot be null!", list);
			assertEquals("Wrong size of list!", 1, list.size());

			result = daoWithTransaction.deleteStats(stats.getId());
			assertTrue("deleteTClass() must be true!", result);

			DatabaseManager.get().closeAndCommitTransaction(token);
		} catch (Exception ex) {
			DatabaseManager.get().rollbackTransaction(token);
			fail(ex.getMessage());
		}
	}

	// ------------------------------------ private methods
	private TestStatistics buildTestStatistics(int begin) {

		TestStatistics ts = new TestStatistics();
		ts.setRunning(begin++);
		ts.setRunningSuccess(begin++);
		ts.setRunningFailure(begin++);

		ts.setRunningError(begin++);
		ts.setRunningErrorCrash(begin++);
		ts.setRunningErrorTimeout(begin++);
		ts.setRunningErrorException(begin++);

		ts.setSkipped(begin++);
		ts.setSkippedDependency(begin++);
		ts.setSkippedProgrammaticaly(begin++);

		return ts;
	}

	private void assertGetById(DbStatsCategoryInstance stats, DaoStatsCategoryInstance dao) throws Exception {

		DbStatsCategoryInstance dbResult = dao.getById(stats.getId());
		assertNotNull("DbTestClass cannot not be null!", dbResult);

		assertEquals("Wrong id!", stats.getId(), dbResult.getId());

		DbTestSuiteInstance suite = dbResult.getTestSuiteInstance();
		assertNotNull("dbResult.suite cannot be null!", suite);
		assertEquals("Wrong suite!", stats.getTestSuiteInstance(), dbResult.getTestSuiteInstance());

		DbTestClassCategory category = dbResult.getTClassCategory();
		assertNotNull("dbResult.category cannot be null!", category);
		assertEquals("Wrong category!", stats.getTClassCategory(), dbResult.getTClassCategory());

		TestStatistics tStats = stats.getTestStatistics();
		TestStatistics dbtStats = dbResult.getTestStatistics();
		assertNotNull("dbResult.testStatistoc cannot be null!", dbtStats);
		assertEquals("Wrong stats.running!", tStats.getRunning(), dbtStats.getRunning());
		assertEquals("Wrong stats.runningSuccess!", tStats.getRunningSuccess(), dbtStats.getRunningSuccess());
		assertEquals("Wrong stats.runningFailure!", tStats.getRunningFailure(), dbtStats.getRunningFailure());
		assertEquals("Wrong stats.runningError!", tStats.getRunningError(), dbtStats.getRunningError());
		assertEquals("Wrong stats.runningErrorCrash!", tStats.getRunningErrorCrash(), dbtStats.getRunningErrorCrash());
		assertEquals("Wrong stats.runningErrorTimeout!", tStats.getRunningErrorTimeout(),
				dbtStats.getRunningErrorTimeout());
		assertEquals("Wrong stats.runningErrorException!", tStats.getRunningErrorException(),
				dbtStats.getRunningErrorException());
		assertEquals("Wrong stats.Skipped!", tStats.getSkipped(), dbtStats.getSkipped());
		assertEquals("Wrong stats.SkippedDependency!", tStats.getSkippedDependency(), dbtStats.getSkippedDependency());
		assertEquals("Wrong stats.SkippedProgrammaticaly!", tStats.getSkippedProgrammaticaly(),
				dbtStats.getSkippedProgrammaticaly());

	}

}
