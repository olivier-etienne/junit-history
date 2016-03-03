package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoTestSuiteGroup extends AbstractDao<DbTestSuiteGroup> implements IDaoTestSuiteGroup {

	private static final Logger log = Logger.getLogger(DaoTestSuiteGroup.class.getName());

	// ---------------------------------- Singleton
	private static DaoTestSuiteGroup instance;

	public static final DaoTestSuiteGroup get() {
		if (instance == null) {
			instance = new DaoTestSuiteGroup();
		}
		return instance;
	}

	private DaoTestSuiteGroup() {
	}

	private List<DbTestSuiteGroup> cachedListGroups;

	// -------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		// NA
	}

	@Override
	protected DbTestSuiteGroup buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {
		return this.buildGroup(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods

	public void deleteAll() throws JUnitHistoryException {

		this.clearCaches();
		super.updateOneItem(SQL_DELETE_ALL);
	}

	public List<DbTestSuiteGroup> listGroups(boolean cached) throws JUnitHistoryException {

		if (cached && this.cachedListGroups != null) {
			return this.cachedListGroups;
		}

		this.cachedListGroups = super.listEntry(SQL_SELECT_GROUP);
		return this.cachedListGroups;
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	public DbTestSuiteGroup getById(int groupId, boolean cached) throws JUnitHistoryException {

		if (cached) {
			return this.getCachedById(groupId);
		}
		return super.getById(groupId, MF_SELECT_ONE_ENTRY);

	}

	public int countByName(String name) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_NAME.format(new String[] { name }));
	}

	/**
	 * Create a new TestMessage
	 * 
	 * @param testInstance
	 * @return
	 */
	public boolean createGroup(DbTestSuiteGroup group) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(group, SQL_SELECT_MAX_ID);
		boolean result = this.createOrUpdateGroup(group, MF_CREATE_GROUP, true);
		clearCaches();
		return result;
	}

	/**
	 * Update an existing group
	 */
	public boolean updateGroup(DbTestSuiteGroup group) throws JUnitHistoryException {
		this.verifyIdForUpdateEntry(group);
		boolean result = this.createOrUpdateGroup(group, MF_UPDATE_GROUP, false);
		this.clearCaches();
		return result;
	}

	public boolean deleteGroup(int groupId) throws JUnitHistoryException {

		this.clearCaches();
		return super.deleteEntry(groupId, MF_DELETE_ONE_ENTRY);

	}

	// ----------------------------------------- private methods
	/**
	 * Récupère une entrée par son id sur la list cached
	 * 
	 * @param groupId
	 * @return
	 */
	private DbTestSuiteGroup getCachedById(int groupId) throws JUnitHistoryException {

		return super.getCachedById(groupId, this.listGroups(true));

	}

	private DbTestSuiteGroup buildGroup(ResultSet rs) throws JUnitHistoryException {
		try {

			DbTestSuiteGroup entry = new DbTestSuiteGroup(rs.getString(DB_STB), rs.getString(DB_NAME),
					rs.getString(DB_PREFIX));
			entry.setId(rs.getInt(DB_ID));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildMessage(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateGroup(DbTestSuiteGroup entry, MessageFormat sqlToFormat, boolean created)
			throws JUnitHistoryException {

		this.verifyGroup(entry);

		String stb = this.formatSqlString(entry.getStb());
		String name = this.formatSqlString(entry.getName());
		String prefix = this.formatSqlString(entry.getPrefix());

		String sql = created ? sqlToFormat.format(new Object[] { entry.getId(), stb, name, prefix }) : sqlToFormat
				.format(new Object[] { stb, name, prefix, entry.getId() });

		return this.updateOneItem(sql);

	}

	private void verifyGroup(DbTestSuiteGroup entry) throws JUnitHistoryException {

		String prefix = "DbTestSuiteGroup";
		super.verifyEntryBeforeSave(prefix, entry);

		// name required
		super.verifyNotNull(prefix + " name", entry.getName());

		// stb required
		super.verifyNotNull(prefix + " stb", entry.getStb());

		// prefix required
		super.verifyNotNull(prefix + " prefix", entry.getPrefix());

	}

	private void clearCaches() {
		this.cachedListGroups = null;
	}

}
