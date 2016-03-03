package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;

public class DaoTestSuiteInstance extends AbstractDao<DbTestSuiteInstance> implements IDaoTestSuiteInstance {

	private static final Logger log = Logger.getLogger(DaoTestSuiteInstance.class.getName());

	// ---------------------------------- Singleton
	private static DaoTestSuiteInstance instance;
	private static final Map<String, DaoTestSuiteInstance> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestSuiteInstance>());

	private static final String PARAM_TUSER_LIST = "paramTUserList";

	public static final DaoTestSuiteInstance get() {
		if (instance == null) {
			instance = new DaoTestSuiteInstance();
		}
		return instance;
	}

	public static final DaoTestSuiteInstance getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestSuiteInstance dao = new DaoTestSuiteInstance(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestSuiteInstance() {
		this(null);
	}

	private DaoTestSuiteInstance(String token) {
		super(token);
	}

	// ----------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbTestSuiteInstance buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {

		@SuppressWarnings("unchecked")
		Map<Integer, DbTestUser> mapId2TUser = (params == null) ? null : (Map<Integer, DbTestUser>) params
				.get(PARAM_TUSER_LIST);
		return this.buildSuite(rs, mapId2TUser);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// --------------------------------- public methods
	public int count() throws JUnitHistoryException {
		return super.count(SQL_COUNT_SUITE);
	}

	public int countByName(String name) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_NAME.format(new String[] { name }));
	}

	public int countByGroup(int groupId) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_GROUP.format(new Integer[] { groupId }));
	}

	public int countByUser(int userId) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_USER.format(new Integer[] { userId }));
	}

	/**
	 * 
	 * @return map [userId, count(userId)]
	 */
	public Map<Integer, Integer> countUsers() throws JUnitHistoryException {

		return super.buildMapId2Value(COUNT_USERS);
	}

	/**
	 * 
	 * @return map [groupId, count(groupId)]
	 */
	public Map<Integer, Integer> countGroups() throws JUnitHistoryException {

		return super.buildMapId2Value(COUNT_GROUPS);
	}

	public List<DbTestSuiteInstance> listSuites() throws JUnitHistoryException {

		if (this.count() == 0) {
			return new ArrayList<>(0);
		}

		return this.listSuitesAggregateUser(SQL_SELECT_SUITE);
	}

	/**
	 * Retourne la liste des suites d'un group classées par ordre decroissant de
	 * date et de nom (les plus recent sont en premier)
	 */
	public List<DbTestSuiteInstance> listSuitesByGroup(int groupId) throws JUnitHistoryException {

		if (this.countByGroup(groupId) == 0) {
			return new ArrayList<>(0);
		}

		return this.listSuitesAggregateUser(MF_SELECT_BY_GROUP.format(new Integer[] { groupId }));
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	public DbTestSuiteInstance getById(int suiteId) throws JUnitHistoryException {

		return super.getById(suiteId, MF_SELECT_ONE_ENTRY);

	}

	/**
	 * Récupère une entrée par son name
	 * 
	 * @param videoId
	 * @return
	 */
	public DbTestSuiteInstance getByName(String name) throws JUnitHistoryException {

		return super.getUniqueByValue(name, MF_SELECT_BY_NAME);

	}

	/**
	 * Create a new TestSuiteInstance
	 * 
	 * @param testSuiteInstance
	 * @return
	 */
	public boolean createSuite(DbTestSuiteInstance testSuiteInstance) throws JUnitHistoryException {
		super.verifyIdForCreateEntry(testSuiteInstance, SQL_SELECT_MAX_ID);
		return this.createSuite(testSuiteInstance, MF_CREATE_SUITE);
	}

	public boolean deleteSuite(int suiteId) throws JUnitHistoryException {

		return super.deleteEntry(suiteId, MF_DELETE_ONE_ENTRY);

	}

	/**
	 * Met à jour les informations d'une suite (Edit report)
	 */
	public boolean updateSuiteInfo(VoTestSuiteForEdit suiteToUpdate) throws JUnitHistoryException {

		this.verifySuiteToUpdate(suiteToUpdate);

		String iptvkit = this.formatSqlString(suiteToUpdate.getIptvkit());
		String comment = this.formatSqlString(suiteToUpdate.getComment());
		long dateTs = (suiteToUpdate.getDate() != null) ? suiteToUpdate.getDate().getTime() : IVo.ID_UNDEFINED;
		int userId = suiteToUpdate.getUserId();

		String sql = SQL_UPDATE_SUITE_INFO.format(new Object[] { iptvkit, dateTs, userId, comment,
				suiteToUpdate.getId() });
		return this.updateOneItem(sql);
	}

	// ------------------------------------ private
	private List<DbTestSuiteInstance> listSuitesAggregateUser(String sql) throws JUnitHistoryException {
		// Aggregation with DbTestUser
		Map<Integer, DbTestUser> mapId2TUser = DaoTestUser.getWithTransaction(this.getToken()).getMapId2TUser(true);

		return super.listEntry(sql, ObjectUtils.buildMapWithOneItem(PARAM_TUSER_LIST, mapId2TUser));

	}

	private DbTestSuiteInstance buildSuite(ResultSet rs, Map<Integer, DbTestUser> mapId2TUser)
			throws JUnitHistoryException {
		try {
			DbTestSuiteInstance entry = new DbTestSuiteInstance();

			// group
			int groupId = rs.getInt(DB_GROUP_ID);
			entry.setTestSuiteGroup(DaoTestSuiteGroup.get().getById(groupId, true));

			// user
			int userId = rs.getInt(DB_USER_ID);
			DbTestUser user = (mapId2TUser != null) ? mapId2TUser.get(userId) : DaoTestUser.getWithTransaction(
					this.getToken()).getById(userId);
			entry.setUser(user);

			entry.setId(rs.getInt(DB_ID));
			entry.setName(rs.getString(DB_NAME));

			entry.setComment(this.getValueNotEmpty(rs.getString(DB_COMMENT)));

			long datets = rs.getLong(DB_DATE);
			entry.setDate(datets > 0 ? new Date(datets) : null);
			entry.setFirmware(rs.getString(DB_FIRMWARE));
			entry.setIptvkit(rs.getString(DB_IPTVKIT));
			entry.setLogExists(rs.getBoolean(DB_LOG));
			entry.setTime(rs.getLong(DB_TIME));
			entry.setReadonly(rs.getBoolean(DB_READ_ONLY));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildDbTestSuiteInstance(): " + e.getMessage());
		}

	}

	private boolean createSuite(DbTestSuiteInstance suite, MessageFormat sqlToFormat) throws JUnitHistoryException {

		this.verifySuiteToCreate(suite);

		String name = this.formatSqlString(suite.getName());
		String comment = this.formatSqlString(suite.getComment());
		String firmware = this.formatSqlString(suite.getFirmware());
		String iptvkit = this.formatSqlString(suite.getIptvkit());
		long dateTs = (suite.getDate() != null) ? suite.getDate().getTime() : IVo.ID_UNDEFINED;

		int groupId = suite.getTestSuiteGroup().getId();
		int userId = this.getIdFromDbEntry(suite.getUser());

		String sql = sqlToFormat.format(new Object[] { suite.getId(), name, firmware, iptvkit, comment, dateTs,
				suite.isLogExists(), suite.getTime(), groupId, userId, suite.isReadonly() });

		return this.updateOneItem(sql);

	}

	private boolean verifySuiteToUpdate(VoTestSuiteForEdit voSuite) throws JUnitHistoryException {

		final String prefix = "DbTestSuiteInstance.";
		this.verifyNotNull(prefix, voSuite);

		if (voSuite.getId() == IVo.ID_UNDEFINED) {
			throw new JUnitHistoryException("undefined suite cannot be updated!");
		}

		return true;

	}

	private boolean verifySuiteToCreate(DbTestSuiteInstance entry) throws JUnitHistoryException {

		final String prefix = "DbTestSuiteInstance.";
		this.verifyNotNull(prefix, entry);

		// Name required
		this.verifyNotNull(prefix + "name", entry.getName());
		// firmware required
		this.verifyNotNull(prefix + "firmware", entry.getFirmware());
		// groupId
		this.verifyNotNull(prefix + "group", entry.getTestSuiteGroup());

		return true;
	}

}
