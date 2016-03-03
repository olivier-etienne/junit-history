package com.francetelecom.orangetv.junithistory.server.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;

public class DaoTestClassTest extends AbstractTest {

	private static final Logger log = Logger.getLogger(DaoTestClassTest.class.getName());

	private final static DaoTestClass dao = DaoTestClass.get();

	@Test
	public void testListTClasses() throws Exception {

		List<DbTestClass> result = dao.listTClasses();
		assertNotNull("list of DbTestClass cannot be null!", result);
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestClass result = dao.getById(9999);
		assertNull("DbTestClass must be null!", result);
	}

	@Test
	public void testDeleteTClass_notExist() throws Exception {

		boolean result = dao.deleteTClass(9999);
		assertFalse("deleteTClass() must be false!", result);
	}

	@Test
	public void testCreateTClass() throws Exception {

		DbTestClass tclass = this.createTClass("test tClass", 2);
		this.assertGetById(tclass);
		this.assertByName(tclass);

		// modify category
		boolean result = dao.updateCategoryId(tclass.getId(), 3);
		assertTrue("updateCategoryId() must return true!", result);

		result = dao.deleteTClass(tclass.getId());
		assertTrue("deleteTClass() must be true!", result);
	}

	// ------------------------------------ private methods
	private DbTestClass createTClass(String name, int categoryId) throws Exception {
		DbTestClass tclass = new DbTestClass(name);

		DbTestClassCategory category = DaoTestClassCategory.get().getById(categoryId, false);
		assertNotNull("category cannot be null!", category);
		tclass.setCategory(category);

		boolean result = dao.createTClass(tclass);
		assertTrue("createTClass() must return true!", result);

		return tclass;

	}

	private void assertByName(DbTestClass tclass) throws Exception {
		DbTestClass dbResult = dao.getByName(tclass.getName());
		this.assertGetBy(tclass, dbResult);
	}

	private void assertGetById(DbTestClass tclass) throws Exception {

		DbTestClass dbResult = dao.getById(tclass.getId());
		this.assertGetBy(tclass, dbResult);
	}

	private void assertGetBy(DbTestClass tclass, DbTestClass dbResult) throws Exception {
		assertNotNull("DbTestClass cannot not be null!", dbResult);

		assertEquals("Wrong id!", tclass.getId(), dbResult.getId());
		assertEquals("Wrong name!", tclass.getName(), dbResult.getName());
		assertEquals("Wrong category!", tclass.getCategory(), dbResult.getCategory());
		assertNotNull("category cannot be null!", dbResult.getCategory());
	}

}
