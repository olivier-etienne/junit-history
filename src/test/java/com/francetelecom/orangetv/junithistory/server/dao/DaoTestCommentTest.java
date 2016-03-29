package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestComment;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;

/**
 * JUnit tests for DaoTestComment
 * 
 * @author ndmz2720
 *
 */
public class DaoTestCommentTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoTestMessageTest.class.getName());

	private final static DaoTestComment dao = DaoTestComment.get();

	@Test
	public void testListComments() throws Exception {

		List<DbTestComment> result = dao.listTComments(true);
		assertNotNull("list of DbTestMessage cannot be null!", result);

		for (DbTestComment dbTestComment : result) {
			log.info("id: " + dbTestComment.getId() + " - " + dbTestComment.getTitle());
			assertNotNull("user cannot be null!!", dbTestComment.getTester());
			assertTrue("user must be lazy!", dbTestComment.getTester().isLazy());
		}

		result = dao.listTComments(false);
		assertNotNull("list of DbTestMessage cannot be null!", result);

		for (DbTestComment dbTestComment : result) {
			log.info("id: " + dbTestComment.getId() + " - " + dbTestComment.getTitle());

			DbTestUser user = dbTestComment.getTester();
			assertNotNull("user cannot be null!!", user);
			assertFalse("user cannot be be lazy!", user.isLazy());

			assertNotNull("user.name cannot be null!", user.getName());
		}

	}

	@Test
	public void testListCommensForSuite() throws Exception {
		List<DbTestComment> result = dao.listCommentForSuite(1);
		assertNotNull("list of DbTestComment cannot be null!", result);

		for (DbTestComment dbTComment : result) {
			log.info("comment: " + dbTComment.getTitle());
		}
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestComment result = dao.getById(9999);
		assertNull("DbTestComment must be null!", result);
	}

	@Test
	public void testGetByTest() throws Exception {

		DbTestComment result = dao.getByTest(3);
		assertNotNull("DbTestComment cannot be null!", result);

		result = dao.getByTest(9999);
		assertNull("DbTestComment must be null!", result);

	}

	@Test
	public void testCreateCommentWithTest() throws Exception {
		String token = "toto";
		DaoTestInstance daoTestForTransaction = DaoTestInstance.getWithTransaction(token);
		DaoTestComment daoCommentForTransaction = DaoTestComment.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);

			// Get user admin
			DbTestUser admin = DaoTestUser.get().getUserAdmin();
			assertNotNull("user admin cannot be null!", admin);

			DbTestSuiteInstance suite = new LazyTestSuiteInstance(5);
			DbTestClass tclass = new DbTestClass("myclass bidon");

			// create en base du test
			DbTestInstance test = new DbTestInstance(suite, tclass);
			test.setName("test  name");
			test.setStatus(TestSubStatusEnum.success);
			boolean result = daoTestForTransaction.createTest(test);
			assertTrue("createTest() must be true!", result);
			assertTrue("test.id must be defined", test.getId() != IDbEntry.ID_UNDEFINED);

			// creation en base du tcomment
			DbTestComment tcomment = new DbTestComment(new Date(), admin);
			tcomment.setTitle("title for test comment");
			tcomment.setDescription("description for test comment");

			daoCommentForTransaction.createTComment(tcomment, test.getId());
			assertTrue("createTComment() must be true!", result);

			// verification
			this.assertGetById(tcomment, test.getId(), daoCommentForTransaction);

			// update
			VoTestCommentForEdit voComment = new VoTestCommentForEdit(tcomment.getId());
			voComment.setTesterId(tcomment.getTester().getId());
			voComment.setTitle("modified title for test comment");
			voComment.setDescription("modified description for test comment");
			voComment.setTestId(test.getId());

			result = daoCommentForTransaction.updateTComment(voComment);
			assertTrue("updateTComment() must be true!", result);

			// supression du test
			// cascade >> suppression auto du tcomment
			result = daoTestForTransaction.deleteTest(test.getId());
			assertTrue("deleteTest() must be true!", result);

			// verification comment doen't exists!
			DbTestComment verif = daoCommentForTransaction.getById(tcomment.getId());
			assertNull("message " + tcomment.getId() + " cannot not exists any more!", verif);

			DatabaseManager.get().closeAndCommitTransaction(token);

		} catch (Exception e) {
			DatabaseManager.get().rollbackTransaction(token);
			throw e;
		}

	}

	// ------------------------------------ private methods

	private void assertGetById(DbTestComment comment, int testId, DaoTestComment dao) throws Exception {

		DbTestComment dbResult = dao.getById(comment.getId());
		assertNotNull("DbTestComment by id cannot not be null!", dbResult);

		assertEquals("Wrong id!", comment.getId(), dbResult.getId());
		assertEquals("Wrong title!", comment.getTitle(), dbResult.getTitle());
		assertEquals("Wrong description!", comment.getDescription(), dbResult.getDescription());
		assertEquals("Wrong user.id!", comment.getTester().getId(), dbResult.getTester().getId());

		dbResult = dao.getByTest(testId);
		assertNotNull("DbTestComment by testId cannot not be null!", dbResult);

		assertEquals("Wrong id!", comment.getId(), dbResult.getId());
		assertEquals("Wrong title!", comment.getTitle(), dbResult.getTitle());
		assertEquals("Wrong description!", comment.getDescription(), dbResult.getDescription());
		assertEquals("Wrong user.id!", comment.getTester().getId(), dbResult.getTester().getId());

	}

}
