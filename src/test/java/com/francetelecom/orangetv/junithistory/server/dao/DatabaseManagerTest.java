package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;

public class DatabaseManagerTest {

	private static final DatabaseManager manager = DatabaseManager.get();

	@Test
	public void testGetConnection() throws Exception {

		Connection con = null;
		try {
			con = manager.getConnection(null);
			assertNotNull("con cannot be null!!", con);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {

			manager.closeConnection(con, null);
		}
	}

	@Test
	public void testGetConnectionForTransaction() throws Exception {

		String token = "titi";

		try {

			manager.beginTransaction(token);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			manager.closeAndCommitTransaction(token);
		}

	}

	@Test
	public void testGetConnectionForTransactionAndRollback() throws Exception {

		String token = "titi";

		try {

			manager.beginTransaction(token);
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			manager.rollbackTransaction(token);
		}

	}

}
