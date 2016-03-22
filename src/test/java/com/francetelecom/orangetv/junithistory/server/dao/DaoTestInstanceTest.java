package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestComment;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;

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
			if (dbTestInstance.getComment() != null) {
				assertFalse(dbTestInstance.getComment().isLazy());
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
			if (dbTestInstance.getComment() != null) {
				assertTrue(dbTestInstance.getComment().isLazy());
			}
		}

	}

	@Test
	public void testListTestsForGroupAndTClassAndTestName() throws Exception {

		List<DbTestInstance> result = DaoTestInstance.get().listTestsForGroupIdTClassIdAndTestName(11, -1, "toto");
		assertNotNull("list of DbTestInstance cannot be null!", result);
		assertEquals("No tests with name toto!", 0, result.size());

		result = DaoTestInstance.get().listTestsForGroupIdTClassIdAndTestName(11, 31, "testRecurrentSchedule");
		assertNotNull("list of DbTestInstance cannot be null!", result);
		assertNotSame("There must be tests with name 'testRecurentList'!", 0, result.size());

		for (DbTestInstance dbTestInstance : result) {
			log.info(dbTestInstance.toString());
			if (dbTestInstance.getMessage() != null) {
				assertTrue(dbTestInstance.getMessage().isLazy());
			}

			// test suite
			DbTestSuiteInstance testSuite = dbTestInstance.getTestSuiteInstance();
			assertNotNull("Suite cannot be null!", testSuite);
			assertFalse("Suite cannot be lazy!", testSuite.isLazy());
		}

	}

	@Test
	public void testlistTClassForGroupAndName() throws Exception {

		List<VoIdName> result = DaoTestInstance.get().listTClassForGroupAndName(11, "toto");
		assertNotNull("list of tclass cannot be null", result);

		result = DaoTestInstance.get().listTClassForGroupAndName(11, "testRecurrentSchedule");
		assertNotNull("list of tclass cannot be null", result);
		assertNotSame("Wrong size for count tclass 'Schedule'", 0, result.size());

		for (VoIdName voIdName : result) {
			log.info("id: " + voIdName.getId() + " - name: " + voIdName.getName());
		}
	}

	@Test
	public void testCountTestsForGroupAndContainsName() throws Exception {

		int count = DaoTestInstance.get().countTestsForGroupAndContainsName(3, "toto");
		assertEquals("Wrong count tests for name 'toto'!", 0, count);

		count = DaoTestInstance.get().countTestsForGroupAndContainsName(11, "Schedule");
		log.info("count of tests: " + count);
		assertNotSame("Wrong count tests for name 'Schedule'!", 0, count);
	}

	@Test
	public void testListDistinctNamesForGroupAndTestName() throws Exception {

		List<VoIdName> result = DaoTestInstance.get().searchDistinctNamesForGroupAndContainsName(3, "toto");
		assertNotNull("list of tests cannot be null", result);
		assertEquals("Wrong size for count test 'toto'", 0, result.size());

		result = DaoTestInstance.get().searchDistinctNamesForGroupAndContainsName(11, "Schedule");
		assertNotNull("list of tests cannot be null", result);
		assertNotSame("Wrong size for count test 'toto'", 0, result.size());

		for (VoIdName distinctName : result) {
			log.info(distinctName.getName());
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

		// assertNotNull("test.comment cannot be null!", result.getComment());
		// assertNotNull("test.message cannot be null!", result.getMessage());
	}

	@Test
	public void testDeleteTest_notExist() throws Exception {

		boolean result = DaoTestInstance.get().deleteTest(9999);
		assertFalse("deleteTest must be false!", result);
	}

	// @Test
	// public void testDeleteTest() throws Exception {
	//
	// boolean result = DaoTestInstance.get().deleteTest(7980);
	// assertTrue("deleteTest must be true!", result);
	// }

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
		DaoTestComment daoTCommentForTransaction = DaoTestComment.getWithTransaction(token);
		DaoTestMessage daoTMessageForTransaction = DaoTestMessage.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);

			DbTestSuiteInstance suite = new LazyTestSuiteInstance(5);

			DbTestClass tclass = this.createTClass("class for test", 3, daoTClassForTransaction);

			DbTestInstance test = new DbTestInstance(suite, tclass);
			test.setName("test  name");
			test.setStatus(TestSubStatusEnum.success);

			suite.setTime(22222222);

			boolean result = daoForTransaction.createTest(test);
			assertTrue("createTest() must be true!", result);
			log.info("created test: " + test.getId());

			DbTestMessage tmessage = this.createMessage("type message test", test.getId(), daoTMessageForTransaction);
			DbTestComment tcomment = this.createComment("comment for test createTest", test.getId(),
					daoTCommentForTransaction);

			test.setMessage(tmessage);
			test.setComment(tcomment);

			this.assertGetById(test, daoForTransaction);

			result = daoForTransaction.deleteTest(test.getId());
			assertTrue("deleteTest() must be true!", result);

			result = daoTClassForTransaction.deleteTClass(tclass.getId());

			DatabaseManager.get().closeAndCommitTransaction(token);

		} catch (Exception e) {
			DatabaseManager.get().rollbackTransaction(token);
			throw e;
		}

	}

	// ------------------------------------ private methods
	private DbTestComment createComment(String title, int testId, DaoTestComment dao) throws Exception {
		DbTestComment tcomment = new DbTestComment(new Date(), DaoTestUser.get().getUserAdmin());
		tcomment.setTitle(title);
		tcomment.setDescription("description " + title);

		boolean result = dao.createTComment(tcomment, testId);
		assertTrue("createTComment() cannot return false!", result);
		log.info("Created tcomment: " + tcomment.getId());

		return tcomment;
	}

	private DbTestMessage createMessage(String type, int testId, DaoTestMessage dao) throws Exception {

		DbTestMessage tmessage = new DbTestMessage(type);
		tmessage.setMessage("message for test message");

		boolean result = dao.createMessage(tmessage, testId);
		assertTrue("createMessage() cannot return false!", result);

		log.info("Created tmessage: " + tmessage.getId());
		return tmessage;
	}

	private DbTestClass createTClass(String name, int categoryId, DaoTestClass dao) throws Exception {
		DbTestClass tclass = new DbTestClass(name);

		DbTestClassCategory category = DaoTestClassCategory.get().getById(categoryId, false);
		assertNotNull("category cannot be null!", category);
		tclass.setCategory(category);

		boolean result = dao.createTClass(tclass);
		assertTrue("createTClass() must return true!", result);

		log.info("Created tclass: " + tclass.getId());

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

		assertNotNull("comment cannot be null!", dbResult.getComment());
		assertEquals("Wrong comment.id!", test.getComment().getId(), dbResult.getComment().getId());

		assertNotNull("testSuiteInstance cannot be null!", dbResult.getTestSuiteInstance());
		assertEquals("Wrong suite.id!", test.getTestSuiteInstance().getId(), dbResult.getTestSuiteInstance().getId());

		assertNotNull("testClass cannot be null", dbResult.gettClass());
		assertEquals("Wrong suite.id!", test.gettClass(), dbResult.gettClass());

	}
}
