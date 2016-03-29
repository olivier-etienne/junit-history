package com.francetelecom.orangetv.junithistory.server.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClass;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestMessage;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoManager implements IManager {

	private static final Logger log = Logger.getLogger(DaoManager.class.getName());

	// ----------------------- singleton

	private static DaoManager instance;

	public static DaoManager get() {
		if (instance == null) {
			instance = new DaoManager();
		}
		return instance;
	}

	private DaoManager() {
	}

	// ------------------------------- public methods
	/**
	 * Supprime une suite et de ses tests associes en base
	 * 
	 * @param testSuite
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTestSuite(int suiteId, String sessionId) throws JUnitHistoryException {

		final DbTestSuiteInstance suite = DaoTestSuiteInstance.get().getById(suiteId);
		if (suite == null) {
			return false;
		}

		log.info("BEGIN delete TestSuite.........");
		// manage in one transaction
		String token = sessionId;
		DatabaseManager.get().beginTransaction(token);

		try {
			// delete stats for test suite
			DaoStatsCategoryInstance.getWithTransaction(token).deleteStatsForTestSuite(suiteId);

			// delete tests for test suite (message delete cascade)
			DaoTestInstance.getWithTransaction(token).deleteTestsForTestSuite(suiteId);

			// delete suite
			boolean result = DaoTestSuiteInstance.getWithTransaction(token).deleteSuite(suiteId);
			log.info("........... delete TestSuite ENDED.");
			if (result) {
				DatabaseManager.get().closeAndCommitTransaction(token);
				return true;
			} else {
				DatabaseManager.get().rollbackTransaction(token);
				return false;
			}

		} catch (Exception ex) {
			DatabaseManager.get().rollbackTransaction(token);
			throw ex;

		}
	}

	/**
	 * Sauvegarde d'une suite et de ses tests associes en base
	 * 
	 * @param testSuite
	 * @throws JUnitHistoryException
	 */
	public boolean saveTestSuite(DtoTestSuiteInstance dtoTestSuite, String sessionId) throws JUnitHistoryException {

		if (dtoTestSuite == null) {
			return false;
		}

		log.info("BEGIN save TestSuite.........");

		final DbTestSuiteInstance suite = dtoTestSuite.getTestSuiteInstance();
		if (suite == null || suite.getId() != IDbEntry.ID_UNDEFINED) {
			return false;
		}

		final List<DbTestInstance> listTests = dtoTestSuite.getListDbTestInstances();
		if (listTests == null || listTests.isEmpty()) {
			return false;
		}

		// manage in one transaction
		String token = sessionId;
		DatabaseManager.get().beginTransaction(token);

		try {
			// sauvegarde de la suite
			boolean result = DaoTestSuiteInstance.getWithTransaction(token).createSuite(suite);

			if (result) {
				log.info("suite " + suite.getName() + " saved...");

				// sauvegarde de la liste des tests + testClass + testMessage
				DaoTestClass daotclass = DaoTestClass.getWithTransaction(token);
				DaoTestMessage daoMess = DaoTestMessage.getWithTransaction(token);
				DaoTestInstance daoTest = DaoTestInstance.getWithTransaction(token);

				final Map<String, DbTestClass> mapName2TClass = new HashMap<>();

				for (DbTestInstance dbTestInstance : listTests) {

					// on verifie que la tclass est bien en base
					DbTestClass tclassInDb = this
							.verifyTestClass(dbTestInstance.getTClass(), mapName2TClass, daotclass);
					dbTestInstance.settClass(tclassInDb);

					// sauvegarde du test
					if (daoTest.createTest(dbTestInstance)) {
						log.info("Test " + dbTestInstance.getName() + " saved...");

						// sauvegarde du message
						DbTestMessage dbTestMessage = dbTestInstance.getMessage();
						if (dbTestMessage != null && dbTestMessage.getId() == IDbEntry.ID_UNDEFINED) {
							daoMess.createMessage(dbTestMessage, dbTestInstance.getId());
						}
					}

				}

				// sauvegarde des statistics
				List<DbStatsCategoryInstance> listStats = dtoTestSuite.getListDbStatsCategoryInstances();
				if (listStats != null) {
					DaoStatsCategoryInstance daoStats = DaoStatsCategoryInstance.getWithTransaction(token);
					for (DbStatsCategoryInstance dbStatsCategoryInstance : listStats) {
						daoStats.createStats(dbStatsCategoryInstance);
					}
				}
			}

			log.info("........... save TestSuite ENDED.");
			DatabaseManager.get().closeAndCommitTransaction(token);
			return true;
		} catch (Exception ex) {
			DatabaseManager.get().rollbackTransaction(token);
			throw ex;

		}
	}

	/**
	 * Charge depuis la base de données tous les objets relatifs à une suite de
	 * test
	 * 
	 * @param suiteId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public DtoTestSuiteInstance loadTestSuite(int suiteId) throws JUnitHistoryException {

		DbTestSuiteInstance suite = DaoTestSuiteInstance.get().getById(suiteId);
		if (suite == null) {
			throw new JUnitHistoryException("Unable to get suite with id: " + suiteId + "!");
		}

		return this.loadTestSuite(suite);

	}

	public DtoTestSuiteInstance loadTestSuite(DbTestSuiteInstance suite) throws JUnitHistoryException {

		if (suite == null) {
			throw new JUnitHistoryException("Suite cannot be null !");
		}

		// list des tests
		List<DbTestInstance> listTests = DaoTestInstance.get().listTestsForSuite(suite.getId(), false);
		// list des stats
		List<DbStatsCategoryInstance> listStats = DaoStatsCategoryInstance.get().listStatsForSuite(suite.getId());

		DtoTestSuiteInstance dtoTestSuite = new DtoTestSuiteInstance(suite, listTests, listStats);

		return dtoTestSuite;

	}

	// ------------------------------------------- private methods
	private DbTestClass verifyTestClass(DbTestClass tclass, final Map<String, DbTestClass> mapName2TClass,
			DaoTestClass daotclass) throws JUnitHistoryException {

		DbTestClass tclassInDb = tclass;
		if (tclass != null && tclass.getId() == IDbEntry.ID_UNDEFINED) {

			// d'abord on cherche dans la map
			tclassInDb = mapName2TClass.get(tclass.getName());
			if (tclassInDb == null) {

				// ensuite on cherche en base
				tclassInDb = daotclass.getByName(tclass.getName());
				if (tclassInDb == null) {
					// pas en base.. on la cree
					if (daotclass.createTClass(tclass)) {
						tclassInDb = tclass;
						log.config("TestClass " + tclass.getName() + " saved...");
					}
				}
				// on sauvegarde dans la map
				mapName2TClass.put(tclassInDb.getName(), tclassInDb);

			}
		}

		return tclassInDb;
	}
}
