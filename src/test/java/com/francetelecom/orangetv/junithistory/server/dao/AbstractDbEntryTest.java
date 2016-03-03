package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

public class AbstractDbEntryTest extends AbstractTest {

	@Test
	public void testEquals_IdUndefined() {

		DbTestClass tclass1 = new DbTestClass("toto");
		DbTestClass tclass2 = new DbTestClass("toto");

		assertNotSame("tclass1 & tclass2 cannot be equals!", tclass1, tclass2);

		DbTestClass tclass3 = tclass1;
		assertEquals("tclass1 & tclass3 must be equals!", tclass1, tclass3);

		Map<DbTestClass, String> map = new HashMap<DbTestClass, String>();
		map.put(tclass1, tclass1.getName());
		map.put(tclass2, tclass2.getName());
		assertEquals("wrong size!", 2, map.size());

		map.put(tclass3, tclass3.getName());
		assertEquals("wrong size!", 2, map.size());
	}

	@Test
	public void testEquals_IdDefined() {

		DbTestClass tclass1 = new DbTestClass("toto");
		tclass1.setId(1);

		DbTestClass tclass2 = new DbTestClass("toto");
		tclass2.setId(2);

		assertNotSame("tclass1 & tclass2 cannot be equals!", tclass1, tclass2);

		DbTestClass tclass3 = new DbTestClass("titi");
		tclass3.setId(1);
		assertEquals("tclass1 & tclass3 must be equals!", tclass1, tclass3);
		assertFalse("tclass1 == tclass3 must be false!", tclass1 == tclass3);

		Map<DbTestClass, String> map = new HashMap<DbTestClass, String>();
		map.put(tclass1, tclass1.getName());
		map.put(tclass2, tclass2.getName());
		assertEquals("wrong size!", 2, map.size());

		map.put(tclass3, tclass3.getName());
		assertEquals("wrong size!", 2, map.size());

	}

}
