package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoTestSuiteGroupTest extends AbstractTest {

	private final static String[][] TAB_GROUP_NAME = new String[][] { { "EsR1-", "AirTies-R1", "Airties" },
			{ "EsR2-", "AirTies-R2", "Airties" }, { "FrR3-sg-", "FrR3-Sagem", "Sagem FR" },
			{ "FrR3-ss-", "FrR3-Samsung", "Samsung FR" }, { "FrR4-sg-", "FrR4-Sagem", "Sagem FR" },
			{ "FrR4-ss-", "FrR4-Samsung", "Samsung FR" }, { "PLR1-", "PLR1-Sagem", "Sagem PL" },
			{ "PLR2-", "PLR2-Sagem", "Sagem PL" }, { "NBXR2-", "Newbox-RQS5", "Newbox" },
			{ "NBXR2+-", "Newbox-R2Phase2", "Newbox" }, { "NBXR4-", "Newbox-R4", "Newbox" },
			{ "MIB4-", "MIB4", "MIB4" }, { "SNR1-", "SNR1-Sagem", "Sagem SN" },
			{ "PLSag-", "PLSag-Saguaro", "Saguaro" } };

	private static final Logger log = Logger.getLogger(DaoTestSuiteGroupTest.class.getName());

	private final static DaoTestSuiteGroup dao = DaoTestSuiteGroup.get();

	@Test
	public void testListGroups() throws Exception {

		List<DbTestSuiteGroup> result = dao.listGroups(false);
		assertNotNull("list of DbTestSuiteGroup cannot be null!", result);
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestSuiteGroup result = dao.getById(9999, false);
		assertNull("DbTestSuiteGroup must be null!", result);
	}

	@Test
	public void testDeleteGroup_notExist() throws Exception {

		boolean result = dao.deleteGroup(9999);
		assertFalse("deleteGroup must be false!", result);
	}

	@Test
	public void testCreateGroup() throws Exception {

		DbTestSuiteGroup group = new DbTestSuiteGroup("stb", "group name", "group prefix");

		boolean result = dao.createGroup(group);
		assertTrue("createGroup() must be true!", result);

		this.assertGetById(group);

		DbTestSuiteGroup groupToUpdate = new DbTestSuiteGroup("stb2", "group name2", "group prefix2");
		groupToUpdate.setId(group.getId());
		result = dao.updateGroup(groupToUpdate);
		assertTrue("updateGroup() must be true!", result);

		result = dao.deleteGroup(group.getId());
		assertTrue("deleteGroup() must be true!", result);
	}

	/*
	 * INIT TABLE
	 */
	@Test
	public void testCreateListGroups() throws Exception {

		List<DbTestSuiteGroup> list = this.getListTestGroups();

		int id = 0;
		for (DbTestSuiteGroup group : list) {
			group.setId(id++);
			boolean result = dao.createGroup(group);
			assertTrue("createGroup() must be true!", result);
		}

		// verification
		List<DbTestSuiteGroup> result = dao.listGroups(true);
		assertNotNull("list of groups cannot be null!", result);
		assertEquals("Wrong size of groups!", list.size(), result.size());

		for (DbTestSuiteGroup group : result) {
			log.info(group.toString());
		}
	}

	/*
	 * CLEAR TABLE
	 */
	@Test
	public void testDeleteAll() throws Exception {

		dao.deleteAll();

		// verification
		List<DbTestSuiteGroup> list = dao.listGroups(true);
		assertNotNull("list of groups cannot be null!", list);
		assertEquals("Wrong size of groups!", 0, list.size());
	}

	// ------------------------------------ private methods

	private void assertGetById(DbTestSuiteGroup group) throws Exception {

		DbTestSuiteGroup dbResult = dao.getById(group.getId(), false);
		assertNotNull("DbTestSuiteGroup cannot not be null!", dbResult);

		assertEquals("Wrong id!", group.getId(), dbResult.getId());
		assertEquals("Wrong stb!", group.getStb(), dbResult.getStb());
		assertEquals("Wrong name!", group.getName(), dbResult.getName());
		assertEquals("Wrong prefix!", group.getPrefix(), dbResult.getPrefix());

	}

	private List<DbTestSuiteGroup> getListTestGroups() throws JUnitHistoryException {

		List<DbTestSuiteGroup> listGroup = new ArrayList<>();

		int id = 0;
		for (String[] group : TAB_GROUP_NAME) {

			DbTestSuiteGroup dbGroup = new DbTestSuiteGroup(group[2], group[1], group[0]);
			dbGroup.setId(id++);
			listGroup.add(dbGroup);

		}

		return listGroup;
	}

}
