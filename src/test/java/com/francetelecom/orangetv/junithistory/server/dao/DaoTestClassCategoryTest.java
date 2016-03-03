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

import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;

public class DaoTestClassCategoryTest extends AbstractTest {

	private final String[][] TAB_CLASS_CATEGORIES =

	new String[][] {

	{ "Network_wan_lan", "GwtTestNetwork", "GwtTestLanRemote", "GwtTestWanRemote" }, // ...
			{ "Dtt_Dth", "GwtTestDtt", "GwtTestDth" }, // ...
			{ "HDD_Device", "GwtTestHDD", "GwtTestDevice" }, // ...
			{ "AudioVideo", "GwtTestVideo", "GwtTestAudio" }, // ...
			{ "DLNA", "GwtTestDmc" }, // ...
			{ "TS", "GwtTestTimeShift" }, // ...
			{ "PVR", "GwtTestPVR" }, // ...
			{ "other", "" }, // ...
	};

	private static final Logger log = Logger.getLogger(DaoTestClassCategoryTest.class.getName());

	private final static DaoTestClassCategory dao = DaoTestClassCategory.get();

	@Test
	public void testLisCategories() throws Exception {

		List<DbTestClassCategory> result = dao.listCategories(false);
		assertNotNull("list of DbTestClassCategory cannot be null!", result);
	}

	@Test
	public void testGetById_notExist() throws Exception {

		DbTestClassCategory result = dao.getById(9999, false);
		assertNull("DbTestClassCategory must be null!", result);
	}

	@Test
	public void testDeleteCategory_notExist() throws Exception {

		boolean result = dao.deleteCategory(9999);
		assertFalse("deleteCategory must be false!", result);
	}

	@Test
	public void testCreateCategory() throws Exception {

		DbTestClassCategory category = new DbTestClassCategory("test classe category");
		category.setSuiteNames(new String[] { "toto", "titi" });
		category.setDefaultValue(true);

		boolean result = dao.createCategory(category);
		assertTrue("createCategory() must be true!", result);

		this.assertGetById(category);

		VoCategoryForEdit voCategory = new VoCategoryForEdit(category.getId(), "new name");
		voCategory.setListClassNames("tata, tutu");
		category.update(voCategory);

		result = dao.updateCategory(category);
		assertTrue("updateCategory() must be true!", result);

		result = dao.deleteCategory(category.getId());
		assertTrue("deleteMessage() must be true!", result);
	}

	/*
	 * INIT TABLE
	 */
	@Test
	public void testCreateListCategories() throws Exception {

		List<DbTestClassCategory> list = this.getListTestClassCategories();

		int id = 0;
		for (DbTestClassCategory dbTestClassCategory : list) {
			dbTestClassCategory.setId(id++);
			boolean result = dao.createCategory(dbTestClassCategory);
			assertTrue("createCategory() must be true!", result);
		}

		// verification
		List<DbTestClassCategory> result = dao.listCategories(false);
		assertNotNull("list of categories cannot be null!", result);
		assertEquals("Wrong size of categories!", list.size(), result.size());

		for (DbTestClassCategory dbTestClassCategory : result) {
			log.info(dbTestClassCategory.toString());
		}

		// default
		DbTestClassCategory defaultCat = dao.getDefaultCategory(true);
		assertNotNull("defautl category cannot be null!", defaultCat);
		assertTrue("cat.defaultValue must be true!", defaultCat.isDefaultValue());
	}

	/*
	 * CLEAR TABLE
	 */
	@Test
	public void testDeleteAll() throws Exception {

		dao.deleteAll();

		// verification
		List<DbTestClassCategory> list = dao.listCategories(true);
		assertNotNull("list of categories cannot be null!", list);
		assertEquals("Wrong size of categories!", 0, list.size());
	}

	// ------------------------------------ private methods

	private void assertGetById(DbTestClassCategory category) throws Exception {

		DbTestClassCategory dbResult = dao.getById(category.getId(), false);
		assertNotNull("DbTestClassCategory cannot not be null!", dbResult);

		assertEquals("Wrong id!", category.getId(), dbResult.getId());
		assertEquals("Wrong name!", category.getName(), dbResult.getName());
		assertEquals("Wrong suitenames.length!", category.getSuiteNames().length, dbResult.getSuiteNames().length);
		assertEquals("Wrong defaultValue!", category.isDefaultValue(), dbResult.isDefaultValue());

	}

	public List<DbTestClassCategory> getListTestClassCategories() throws JUnitHistoryException {

		final List<DbTestClassCategory> listCat = new ArrayList<>(TAB_CLASS_CATEGORIES.length);

		int id = 0;
		for (String[] categorie : TAB_CLASS_CATEGORIES) {

			DbTestClassCategory dbCategory = new DbTestClassCategory(categorie[0]);

			if (dbCategory.getName().equals("other")) {
				dbCategory.setDefaultValue(true);
			}

			String[] list = new String[categorie.length - 1];
			for (int i = 1; i < categorie.length; i++) {
				list[i - 1] = categorie[i];

			}
			dbCategory.setSuiteNames(list);
			dbCategory.setId(id++);
			listCat.add(dbCategory);

		}
		return listCat;

	}

}
