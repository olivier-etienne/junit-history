package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.util.TestStatistics;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoStatsCategoryInstance extends AbstractDao<DbStatsCategoryInstance> implements IDaoStatsCategoryInstance {

	private static final Logger log = Logger.getLogger(DaoStatsCategoryInstance.class.getName());

	// ---------------------------------- Singleton
	private static DaoStatsCategoryInstance instance;
	private static final Map<String, DaoStatsCategoryInstance> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoStatsCategoryInstance>());

	public static final DaoStatsCategoryInstance get() {
		if (instance == null) {
			instance = new DaoStatsCategoryInstance();
		}
		return instance;
	}

	public static final DaoStatsCategoryInstance getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoStatsCategoryInstance dao = new DaoStatsCategoryInstance(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoStatsCategoryInstance() {
		this(null);
	}

	private DaoStatsCategoryInstance(String token) {
		super(token);
	}

	// ---------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbStatsCategoryInstance buildDbEntry(ResultSet rs, Map<String, Object> params)
			throws JUnitHistoryException {
		return this.buildStats(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods

	public int count() throws JUnitHistoryException {
		return super.count(SQL_COUNT_STATS);
	}

	public int countForSuite(int suiteId) throws JUnitHistoryException {
		return super.count(MF_COUNT_STATS_FOR_SUITE.format(new Integer[] { suiteId }));
	}

	public void deleteAll() throws JUnitHistoryException {

		super.updateOneItem(SQL_DELETE_ALL);
	}

	public List<DbStatsCategoryInstance> listStatsForSuite(int suiteId) throws JUnitHistoryException {

		return super.listEntry(MF_SELECT_STATS_FOR_SUITE.format(new Integer[] { suiteId }));
	}

	public List<DbStatsCategoryInstance> listStats() throws JUnitHistoryException {

		return super.listEntry(SQL_SELECT_STATS);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param tclassId
	 * @return
	 */
	public DbStatsCategoryInstance getById(int statsId) throws JUnitHistoryException {

		return super.getById(statsId, MF_SELECT_ONE_ENTRY);

	}

	/**
	 * Create a new DbStatsCategoryInstance
	 * 
	 * @param stats
	 * @return id
	 */
	public boolean createStats(DbStatsCategoryInstance stats) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(stats, SQL_SELECT_MAX_ID);
		return this.createOrUpdateStats(stats, MF_CREATE_STATS);
	}

	public boolean deleteStats(int statsId) throws JUnitHistoryException {

		return super.deleteEntry(statsId, MF_DELETE_ONE_ENTRY);

	}

	public boolean deleteStatsForTestSuite(int suiteId) throws JUnitHistoryException {

		String sql = MF_DELETE_FOR_SUITE.format(new Integer[] { suiteId });
		return this.deleteEntries(sql);

	}

	// -------------------------------------- private method
	private DbStatsCategoryInstance buildStats(ResultSet rs) throws JUnitHistoryException {
		try {

			// suite lazy
			int suiteId = rs.getInt(DB_SUITE_ID);
			DbTestSuiteInstance suite = new LazyTestSuiteInstance(suiteId);

			// category
			int categoryId = rs.getInt(DB_CATEGORY_ID);
			DbTestClassCategory category = DaoTestClassCategory.get().getById(categoryId, true);

			DbStatsCategoryInstance entry = new DbStatsCategoryInstance(suite, category);
			entry.setId(rs.getInt(DB_ID));

			TestStatistics testStatistics = new TestStatistics();
			testStatistics.setRunning(rs.getInt(DB_RUNNING));
			testStatistics.setRunningSuccess(rs.getInt(DB_RUNNING_SUCCESS));
			testStatistics.setRunningFailure(rs.getInt(DB_RUNNING_FAILURE));
			testStatistics.setRunningError(rs.getInt(DB_RUNNING_ERROR));
			testStatistics.setRunningErrorCrash(rs.getInt(DB_RUNNING_ERROR_CRASH));
			testStatistics.setRunningErrorTimeout(rs.getInt(DB_RUNNING_ERROR_TIMEOUT));
			testStatistics.setRunningErrorException(rs.getInt(DB_RUNNING_ERROR_EX));
			testStatistics.setSkipped(rs.getInt(DB_SKIPPED));
			testStatistics.setSkippedDependency(rs.getInt(DB_SKIPPED_DEP));
			testStatistics.setSkippedProgrammaticaly(rs.getInt(DB_SKIPPED_PRO));
			entry.setTestStatistics(testStatistics);

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildStats(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateStats(DbStatsCategoryInstance entry, MessageFormat sqlToFormat)
			throws JUnitHistoryException {

		this.verifyStats(entry);

		int suiteId = entry.getTestSuiteInstance().getId();
		int categoryId = entry.getTClassCategory().getId();

		TestStatistics statistic = (entry.getTestStatistics() == null) ? new TestStatistics() : entry
				.getTestStatistics();

		String sql = sqlToFormat.format(new Object[] { entry.getId(), suiteId, categoryId, statistic.getRunning(),
				statistic.getRunningSuccess(), statistic.getRunningFailure(), statistic.getRunningError(),
				statistic.getRunningErrorCrash(), statistic.getRunningErrorTimeout(),
				statistic.getRunningErrorException(), statistic.getSkipped(), statistic.getSkippedDependency(),
				statistic.getSkippedProgrammaticaly() });

		return this.updateOneItem(sql);

	}

	private boolean verifyStats(DbStatsCategoryInstance entry) throws JUnitHistoryException {

		String prefix = "DbStatsCategoryInstance";
		super.verifyEntryBeforeSave(prefix, entry);

		// suite required
		this.verifyNotNull(prefix + " suite", entry.getTestSuiteInstance());
		// Category required
		this.verifyNotNull(prefix + " category", entry.getTClassCategory());
		return true;
	}

}
