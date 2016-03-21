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
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;

public class DaoTestMessageTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoTestMessageTest.class.getName());

	private final static DaoTestMessage dao = DaoTestMessage.get();

	@Test
	public void testListMessages() throws Exception {

		List<DbTestMessage> result = dao.listMessages();
		assertNotNull("list of DbTestMessage cannot be null!", result);
	}

	@Test
	public void testListMessagesForSuite() throws Exception {
		List<DbTestMessage> result = dao.listMessagesForSuite(1);
		assertNotNull("list of DbTestMessage cannot be null!", result);

		for (DbTestMessage dbTestMessage : result) {
			log.info("message: " + dbTestMessage.getMessage());
		}
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestMessage result = dao.getById(9999);
		assertNull("DbTestMessage must be null!", result);
	}

	@Test
	public void testGetByTest() throws Exception {

		DbTestMessage result = dao.getByTest(3);
		assertNotNull("DbTestMessage cannot be null!", result);

		result = dao.getByTest(9999);
		assertNull("DbTestMessage must be null!", result);

	}

	@Test
	public void testDeleteMessage_notExist() throws Exception {

		boolean result = dao.deleteMessage(9999);
		assertFalse("deleteMessage must be false!", result);
	}

	@Test
	public void testCreateMessageWithTest() throws Exception {
		String token = "toto";
		DaoTestInstance daoTestForTransaction = DaoTestInstance.getWithTransaction(token);
		DaoTestMessage daoMessageForTransaction = DaoTestMessage.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);

			DbTestSuiteInstance suite = new LazyTestSuiteInstance(5);
			DbTestClass tclass = new DbTestClass("myclass bidon");

			// create en base du test
			DbTestInstance test = new DbTestInstance(suite, tclass);
			test.setName("test  name");
			test.setStatus(TestSubStatusEnum.success);
			boolean result = daoTestForTransaction.createTest(test);
			assertTrue("createTest() must be true!", result);
			assertTrue("test.id must be defined", test.getId() != IDbEntry.ID_UNDEFINED);

			// creation en base du message
			DbTestMessage message = new DbTestMessage("type de test message");
			message.setMessage("message for testMessage");
			message.setOutputLog("output logs");

			daoMessageForTransaction.createMessage(message, test.getId());
			assertTrue("createMessage() must be true!", result);

			// verification
			this.assertGetById(message, test.getId(), daoMessageForTransaction);

			// supression du test
			// cascade >> suppression auto du message
			result = daoTestForTransaction.deleteTest(test.getId());
			assertTrue("deleteTest() must be true!", result);

			// verification message doen't exists!
			DbTestMessage verif = daoMessageForTransaction.getById(message.getId());
			assertNull("message " + message.getId() + " cannot not exists any more!", verif);

			DatabaseManager.get().closeAndCommitTransaction(token);

		} catch (Exception e) {
			DatabaseManager.get().rollbackTransaction(token);
		}

	}

	// ------------------------------------ private methods

	private void assertGetById(DbTestMessage message, int testId, DaoTestMessage dao) throws Exception {

		DbTestMessage dbResult = dao.getById(message.getId());
		assertNotNull("DbTestMessage cannot not be null!", dbResult);

		assertEquals("Wrong id!", message.getId(), dbResult.getId());
		assertEquals("Wrong type!", message.getType(), dbResult.getType());
		assertEquals("Wrong message!", message.getMessage(), dbResult.getMessage());
		assertEquals("Wrong stacktrace!", message.getStackTrace(), dbResult.getStackTrace());
		assertEquals("Wrong outputLog!", message.getOutputLog(), dbResult.getOutputLog());

	}

}
