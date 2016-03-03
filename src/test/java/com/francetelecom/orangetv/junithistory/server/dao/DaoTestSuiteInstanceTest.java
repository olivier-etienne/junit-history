package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;

/*
 * 
 * JUnit tests for DaoTestSuiteInstance
 */
public class DaoTestSuiteInstanceTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoTestSuiteInstanceTest.class.getName());

	@Test
	public void testListTestSuiteInstance() throws Exception {

		List<DbTestSuiteInstance> result = DaoTestSuiteInstance.get().listSuites();
		assertNotNull("list of DbTestSuiteInstance cannot be null!", result);
	}

	@Test
	public void testListTestSuiteInstance_ByGroup() throws Exception {

		List<DbTestSuiteInstance> result = DaoTestSuiteInstance.get().listSuitesByGroup(10);
		assertNotNull("list of DbTestSuiteInstance cannot be null!", result);

		for (DbTestSuiteInstance dbTestSuiteInstance : result) {
			log.info("Suite " + dbTestSuiteInstance.getName() + " - date: " + dbTestSuiteInstance.getDate());
		}
	}

	@Test
	public void testCountUsers() throws Exception {

		Map<Integer, Integer> mapId2CountUsers = DaoTestSuiteInstance.get().countUsers();
		assertNotNull("mapId2CountUsers cannot be null!", mapId2CountUsers);
		for (Integer id : mapId2CountUsers.keySet()) {
			log.info("id/count: " + id + " > count " + mapId2CountUsers.get(id));
		}

	}

	@Test
	public void testCountByName() throws Exception {

		DaoTestSuiteInstance.get().countByName("titi");
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestSuiteInstance result = DaoTestSuiteInstance.get().getById(9999);
		assertNull("DbTestSuiteInstance must be null!", result);
	}

	@Test
	public void testDeleteSuite_notExist() throws Exception {

		boolean result = DaoTestSuiteInstance.get().deleteSuite(9999);
		assertFalse("deleteSuite must be false!", result);
	}

	@Test
	public void testCreateTestSuiteInstance() throws Exception {

		String token = "titi";
		DaoTestSuiteInstance dao = DaoTestSuiteInstance.getWithTransaction(token);

		try {
			DatabaseManager.get().beginTransaction(token);

			DbTestUser user = new DbTestUser("my user");
			DaoTestUser.getWithTransaction(token).createUser(user);

			DbTestSuiteInstance suite = new DbTestSuiteInstance();
			suite.setName("test suite name");
			suite.setComment("comment titi");
			suite.setDate(new Date());
			suite.setFirmware("firmware");
			suite.setIptvkit("iptvkit");
			suite.setLogExists(true);
			suite.setTime(22222222);
			suite.setTestSuiteGroup(DaoTestSuiteGroup.get().getById(2, true));
			suite.setUser(user);

			boolean result = dao.createSuite(suite);
			assertTrue("createTestSuiteInstance() must be true!", result);

			this.assertGetById(suite, dao);
			this.assertGetByName(suite, dao);

			// update
			VoTestSuiteForEdit vo = new VoTestSuiteForEdit(suite.getId(), suite.getName());
			vo.setComment("new comment");
			vo.setDate(new Date());
			vo.setIptvkit("new iptvkit");
			vo.setUserId(IVo.ID_UNDEFINED);
			dao.updateSuiteInfo(vo);

			result = dao.deleteSuite(suite.getId());
			assertTrue("deleteSuite() must be true!", result);

			result = DaoTestUser.getWithTransaction(token).deleteUser(user.getId());
			assertTrue("deleteUser() must be true!", result);

			DatabaseManager.get().closeAndCommitTransaction(token);

		} catch (Exception ex) {
			DatabaseManager.get().rollbackTransaction(token);
			fail(ex.getMessage());
		}
	}

	private void assertGetByName(DbTestSuiteInstance suite, DaoTestSuiteInstance dao) throws Exception {
		DbTestSuiteInstance dbResult = dao.getByName(suite.getName());
		this.assertGetBy(suite, dbResult);
	}

	private void assertGetById(DbTestSuiteInstance suite, DaoTestSuiteInstance dao) throws Exception {
		DbTestSuiteInstance dbResult = dao.getById(suite.getId());
		this.assertGetBy(suite, dbResult);
	}

	private void assertGetBy(DbTestSuiteInstance suite, DbTestSuiteInstance dbResult) {
		assertNotNull("DbTestSuiteInstance cannot not be null!", dbResult);

		assertEquals("Wrong id!", suite.getId(), dbResult.getId());
		assertEquals("Wrong name!", suite.getName(), dbResult.getName());
		assertEquals("Wrong comment!", suite.getComment(), dbResult.getComment());
		assertEquals("Wrong Iptvkit!", suite.getIptvkit(), dbResult.getIptvkit());
		assertEquals("Wrong Firmware!", suite.getFirmware(), dbResult.getFirmware());
		assertEquals("Wrong isLogExists!", suite.isLogExists(), dbResult.isLogExists());
		assertEquals("Wrong date!", suite.getDate(), dbResult.getDate());
		assertEquals("Wrong time!", suite.getTime(), dbResult.getTime());

		assertNotNull("group cannot be null!", dbResult.getTestSuiteGroup());
		assertEquals("Wrong group!", suite.getTestSuiteGroup(), dbResult.getTestSuiteGroup());

		assertNotNull("user cannot be null!", dbResult.getUser());
		assertEquals("Wrong user!", suite.getUser(), dbResult.getUser());

	}

}
