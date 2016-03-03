package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

public class DaoTestInstanceTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoTestInstanceTest.class.getName());

	@Test
	public void testCountTests() throws Exception {

		DaoTestInstance.get().count();

	}

	@Test
	public void testListTests() throws Exception {

		List<DbTestInstance> result = DaoTestInstance.get().listTests();
		assertNotNull("list of DbTestInstance cannot be null!", result);

		for (DbTestInstance dbTestInstance : result) {
			log.info(dbTestInstance.toString());
			// lazy loading
			if (dbTestInstance.getMessage() != null) {
				assertTrue(dbTestInstance.getMessage().isLazy());
			}
		}
	}

	@Test
	public void testCountTestsForSuite() throws Exception {

		int count = DaoTestInstance.get().countForSuite(88);
		assertTrue("count must be >= 0!", count >= 0);

	}

	@Test
	public void testListTestsForSuite() throws Exception {

		List<DbTestInstance> result = DaoTestInstance.get().listTestsForSuite(1, false);
		assertNotNull("list of DbTestInstance cannot be null!", result);

		for (DbTestInstance dbTestInstance : result) {
			log.info(dbTestInstance.toString());
			if (dbTestInstance.getMessage() != null) {
				assertFalse(dbTestInstance.getMessage().isLazy());
			}
		}

		// lazy loading
		result = DaoTestInstance.get().listTestsForSuite(1, true);
		assertNotNull("list of DbTestInstance cannot be null!", result);

		for (DbTestInstance dbTestInstance : result) {
			log.info(dbTestInstance.toString());
			if (dbTestInstance.getMessage() != null) {
				assertTrue(dbTestInstance.getMessage().isLazy());
			}
		}

	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestInstance result = DaoTestInstance.get().getById(99999);
		assertNull("DbTestInstance must be null!", result);
	}

	@Test
	public void testGetById() throws Exception {

		DbTestInstance result = DaoTestInstance.get().getById(1);
		assertNotNull("DbTestInstance cannot be null!", result);
	}

	@Test
	public void testDeleteTest_notExist() throws Exception {

		boolean result = DaoTestInstance.get().deleteTest(9999);
		assertFalse("deleteTest must be false!", result);
	}

	@Test
	public void testDeleteTestsForSuite() throws Exception {

		int suiteId = 8;
		int count = DaoTestInstance.get().countForSuite(suiteId);

		if (count > 0) {
			log.info(count + " tests to delete...");

			boolean result = DaoTestInstance.get().deleteTestsForTestSuite(suiteId);
			assertTrue("deleteTestsForSuite() must be true!", result);

			count = DaoTestInstance.get().countForSuite(suiteId);
			assertEquals("all tests must have been deleted!", 0, count);
		} else {
			log.warning("no tests to delete!");

		}
	}

	@Test
	public void testCreateTest() throws Exception {

		String token = "toto";
		DaoTestInstance daoForTransaction = DaoTestInstance.getWithTransaction(token);
		DaoTestClass daoTClassForTransaction = DaoTestClass.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);

			DbTestSuiteInstance suite = new LazyTestSuiteInstance(5);

			DbTestClass tclass = this.createTClass("class for test", 3, daoTClassForTransaction);

			DbTestMessage message = new DbTestMessage("type message");

			DbTestInstance test = new DbTestInstance(suite, tclass);
			test.setName("test  name");
			test.setStatus(TestSubStatusEnum.success);

			suite.setTime(22222222);

			boolean result = daoForTransaction.createTest(test);
			assertTrue("createTest() must be true!", result);

			this.assertGetById(test, daoForTransaction);

			result = daoForTransaction.deleteTest(test.getId());
			assertTrue("deleteTest() must be true!", result);

			result = daoTClassForTransaction.deleteTClass(tclass.getId());

			DatabaseManager.get().closeAndCommitTransaction(token);

		} catch (Exception e) {
			DatabaseManager.get().rollbackTransaction(token);
		}

	}

	// ------------------------------------ private methods
	private DbTestClass createTClass(String name, int categoryId, DaoTestClass dao) throws Exception {
		DbTestClass tclass = new DbTestClass(name);

		DbTestClassCategory category = DaoTestClassCategory.get().getById(categoryId, false);
		assertNotNull("category cannot be null!", category);
		tclass.setCategory(category);

		boolean result = dao.createTClass(tclass);
		assertTrue("createTClass() must return true!", result);

		return tclass;

	}

	private void assertGetById(DbTestInstance test, DaoTestInstance dao) throws Exception {

		DbTestInstance dbResult = dao.getById(test.getId());
		assertNotNull("DbTestInstance cannot not be null!", dbResult);

		assertEquals("Wrong id!", test.getId(), dbResult.getId());
		assertEquals("Wrong name!", test.getName(), dbResult.getName());

		assertEquals("Wrong status!", test.getStatus(), dbResult.getStatus());

		assertNotNull("message cannot be null!", dbResult.getMessage());
		assertEquals("Wrong message.id!", test.getMessage().getId(), dbResult.getMessage().getId());

		assertNotNull("testSuiteInstance cannot be null!", dbResult.getTestSuiteInstance());
		assertEquals("Wrong suite.id!", test.getTestSuiteInstance().getId(), dbResult.getTestSuiteInstance().getId());

		assertNotNull("testClass cannot be null", dbResult.gettClass());
		assertEquals("Wrong suite.id!", test.gettClass(), dbResult.gettClass());

	}
}
