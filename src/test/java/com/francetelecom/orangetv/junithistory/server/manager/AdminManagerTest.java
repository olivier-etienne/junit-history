package com.francetelecom.orangetv.junithistory.server.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.service.AbstractTest;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;

/**
 * JUnit tests for AdminManager
 * 
 * @author ndmz2720
 *
 */
public class AdminManagerTest extends AbstractTest {

	private static final AdminManager manager = AdminManager.get();

	@Test
	public void testCleanAllSuiteInBdd() throws Exception {

		assertTrue("cleanAllSuiteInBdd must be true!", manager.cleanAllSuiteInBdd());
	}

	@Test
	public void testInitializeBddWithArchives() throws Exception {

		String archivePath = "C:/Users/ndmz2720/Documents/temp/rapport_history";
		manager.initializeBddWithArchives(archivePath, false);
	}

	@Test
	public void testUpdateTestCategory() throws Exception {

		// category DLNA
		DbTestClassCategory category = DaoTestClassCategory.get().getById(4, true);
		assertNotNull("category cannot be null!", category);

		VoCategoryForEdit voCategory = new VoCategoryForEdit(category.getId(), category.getName());
		voCategory.setListClassNames("GwtTestDmc "); // aucunne classe ne va
														// correspondre

		VoDatasValidation validation = manager.createOrUpdateTestCategory(voCategory, "12345678");
		assertNotNull("validation cannot be null!", validation);
		assertTrue("result must be valid!", validation.isValid());
	}
}
