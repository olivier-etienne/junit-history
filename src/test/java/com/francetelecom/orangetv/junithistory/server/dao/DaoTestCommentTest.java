package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.model.DbTestComment;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

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
			assertNotNull("user cannot be null!!", dbTestComment.getUser());
			assertTrue("user must be lazy!", dbTestComment.getUser().isLazy());
		}

		result = dao.listTComments(false);
		assertNotNull("list of DbTestMessage cannot be null!", result);

		for (DbTestComment dbTestComment : result) {
			log.info("id: " + dbTestComment.getId() + " - " + dbTestComment.getTitle());

			DbTestUser user = dbTestComment.getUser();
			assertNotNull("user cannot be null!!", user);
			assertFalse("user cannot be be lazy!", user.isLazy());

			assertNotNull("user.name cannot be null!", user.getName());
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

}
