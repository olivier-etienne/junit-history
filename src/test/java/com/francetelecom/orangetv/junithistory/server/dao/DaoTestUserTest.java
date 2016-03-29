package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;

public class DaoTestUserTest extends AbstractTest {

	private final static DaoTestUser dao = DaoTestUser.get();

	@Test
	public void testListUsers() throws Exception {

		List<DbTestUser> result = dao.listUsers(true);
		assertNotNull("list of DbTestUser cannot be null!", result);
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestUser result = dao.getById(9999);
		assertNull("DbTestUser must be null!", result);
	}

	@Test
	public void testDeleteUser_notExist() throws Exception {

		boolean result = dao.deleteUser(9999);
		assertFalse("deleteUser() must be false!", result);
	}

	@Test
	public void testGetUserAdmin() throws Exception {

		DbTestUser admin = dao.getUserAdmin();
		assertNotNull("DbTestUser cannot not be null!", admin);
		assertFalse("user.id must be defined!", admin.getId() == IDbEntry.ID_UNDEFINED);
		assertTrue("user.admin must be true!", admin.isAdmin());
	}

	@Test
	public void testCreateUser() throws Exception {

		DbTestUser user = new DbTestUser("user de test");
		user.setDescription("description for testUser");

		boolean result = dao.createUser(user);
		assertTrue("createUser() must be true!", result);
		assertFalse("user.id must be defined!", user.getId() == IDbEntry.ID_UNDEFINED);
		assertFalse("user.admin must be false!", user.isAdmin());

		this.assertGetById(user);

		VoUserForEdit voUser = new VoUserForEdit();
		voUser.setName("cloned user!");
		voUser.setDescription("cloned description");
		user.update(voUser);
		result = dao.updateUser(user);
		assertTrue("updateUser() must be true!", result);

		result = dao.deleteUser(user.getId());
		assertTrue("deleteUser() must be true!", result);
	}

	// ------------------------------------ private methods

	private void assertGetById(DbTestUser user) throws Exception {

		DbTestUser dbResult = dao.getById(user.getId());
		assertNotNull("DbTestUser cannot not be null!", dbResult);

		assertEquals("Wrong id!", user.getId(), dbResult.getId());
		assertEquals("Wrong name!", user.getName(), dbResult.getName());
		assertEquals("Wrong description!", user.getDescription(), dbResult.getDescription());

	}

}
