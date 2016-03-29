package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoTestUser extends AbstractDao<DbTestUser> implements IDaoTestUser {

	private static final Logger log = Logger.getLogger(DaoTestUser.class.getName());

	private static boolean initDone = false;

	// ---------------------------------- Singleton
	private static DaoTestUser instance;
	private static final Map<String, DaoTestUser> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestUser>());

	public static final DaoTestUser get() {
		if (instance == null) {
			instance = new DaoTestUser();
		}
		return instance;
	}

	public static final DaoTestUser getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestUser dao = new DaoTestUser(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestUser() {
		this(null);
	}

	private DaoTestUser(String token) {
		super(token);
		this.init();
	}

	private void init() {
		if (initDone) {
			return;
		}
		// on s'assure que l'utilisateur admin exist sinon on le cree
		try {
			if (this.getUserAdmin() == null) {

				DbTestUser admin = new DbTestUser("admin");
				admin.setAdmin(true);
				if (this.createUser(admin)) {
					initDone = true;
				} else {
					throw new RuntimeException("Failure when creating default user!");
				}
			}
		} catch (JUnitHistoryException e) {
			log.severe("Unable to get or create default user !");
			throw new RuntimeException("DaoTestUser cannot get or create default user!");
		}

	}

	// ----------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbTestUser buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {
		return this.buildUser(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods

	public List<DbTestUser> listUsers(boolean withAdmin) throws JUnitHistoryException {

		return super.listEntry(withAdmin ? SQL_SELECT_USER : SQL_SELECT_USER_NO_ADMIN);
	}

	public Map<Integer, DbTestUser> getMapId2TUser(boolean withAdmin) throws JUnitHistoryException {

		return super.getMapId2Entry(withAdmin ? SQL_SELECT_USER : SQL_SELECT_USER_NO_ADMIN);

	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	public DbTestUser getById(int userId) throws JUnitHistoryException {

		return super.getById(userId, MF_SELECT_ONE_ENTRY);

	}

	public DbTestUser getUserAdmin() throws JUnitHistoryException {
		return super.getUniqueEntry(SQL_SELECT_USER_ADMIN);
	}

	public int countByName(String name) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_NAME.format(new String[] { name }));
	}

	/**
	 * Create a new DbTestUser
	 * 
	 * @return
	 */
	public boolean createUser(DbTestUser user) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(user, SQL_SELECT_MAX_ID);
		return this.createOrUpdateUser(user, MF_CREATE_USER, true);
	}

	/**
	 * Update an existing user
	 */
	public boolean updateUser(DbTestUser user) throws JUnitHistoryException {
		this.verifyIdForUpdateEntry(user);
		return this.createOrUpdateUser(user, MF_UPDATE_USER, false);
	}

	public boolean deleteUser(int userId) throws JUnitHistoryException {

		return super.deleteEntry(userId, MF_DELETE_ONE_ENTRY);

	}

	// ----------------------------------------- private methods
	private DbTestUser buildUser(ResultSet rs) throws JUnitHistoryException {
		try {

			DbTestUser entry = new DbTestUser(rs.getString(DB_NAME));
			entry.setId(rs.getInt(DB_ID));
			entry.setDescription(this.getValueNotEmpty(rs.getString(DB_DESCR)));
			entry.setAdmin(rs.getBoolean(DB_ADMIN));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildUser(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateUser(DbTestUser entry, MessageFormat sqlToFormat, boolean created)
			throws JUnitHistoryException {

		this.verifyUser(entry);

		String name = this.formatSqlString(entry.getName());
		String description = this.formatSqlString(entry.getDescription());

		String sql = created ? sqlToFormat.format(new Object[] { entry.getId(), name, description, entry.isAdmin() })
				: sqlToFormat.format(new Object[] { name, description, entry.getId() });

		return this.updateOneItem(sql);

	}

	private void verifyUser(DbTestUser entry) throws JUnitHistoryException {

		String prefix = "DbTestUser";
		super.verifyEntryBeforeSave(prefix, entry);

		// name required
		super.verifyNotNull(prefix + " name", entry.getName());

	}

}
