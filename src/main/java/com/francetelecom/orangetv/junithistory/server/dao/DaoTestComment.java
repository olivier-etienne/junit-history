package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestComment;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestUser;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;

/**
 * Commentaire associé à un test
 * 
 * @author ndmz2720
 *
 */
public class DaoTestComment extends AbstractDao<DbTestComment> implements IDaoTestComment {

	private static final Logger log = Logger.getLogger(DaoTestComment.class.getName());

	private static final String PARAM_TUSER_LIST = "paramTUserList";

	// ---------------------------------- Singleton
	private static DaoTestComment instance;
	private static final Map<String, DaoTestComment> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestComment>());

	public static final DaoTestComment get() {
		if (instance == null) {
			instance = new DaoTestComment();
		}
		return instance;
	}

	public static final DaoTestComment getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestComment dao = new DaoTestComment(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestComment() {
		this(null);
	}

	private DaoTestComment(String token) {
		super(token);
	}

	// ---------------------------------------- overriding AbstractDao
	@Override
	protected DbTestComment buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {

		@SuppressWarnings("unchecked")
		Map<Integer, DbTestUser> mapId2TUser = (params == null) ? null : (Map<Integer, DbTestUser>) params
				.get(PARAM_TUSER_LIST);
		return this.buildTComment(rs, mapId2TUser);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	// ------------------------------------------ public methods

	/**
	 * List de tous les commentaires
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<DbTestComment> listTComments(boolean lazyUser) throws JUnitHistoryException {

		Map<String, Object> params = null;
		if (!lazyUser) {
			final List<DbTestUser> listUsers = DaoTestUser.get().listUsers(true);
			final Map<Integer, DbTestUser> mapId2User = VoIdUtils.getMapId2Item(listUsers);
			params = ObjectUtils.buildMapWithOneItem(PARAM_TUSER_LIST, mapId2User);
		}
		return super.listEntry(SQL_SELECT_TCOMMENT, params);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param commentId
	 */
	public DbTestComment getById(int commentId) throws JUnitHistoryException {

		return super.getById(commentId, MF_SELECT_ONE_ENTRY);
	}

	public DbTestComment getByTest(int testId) throws JUnitHistoryException {
		return super.getUniqueEntry(MF_SELECT_FOR_TEST.format(new Integer[] { testId }));
	}

	/**
	 * Create a new TestComment
	 * 
	 * @param testInstance
	 * @return
	 */
	public boolean createTComment(DbTestComment testMessage, int testId) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(testMessage, SQL_SELECT_MAX_ID);

		// testId required
		if (testId == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException("testId must be defined in test comment!");
		}
		return this.createOrUpdateTComment(testMessage, testId, MF_CREATE_COMMENT);
	}

	/**
	 * Suppression d'un commentaire par son id
	 * 
	 * @param commentId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTComment(int commentId) throws JUnitHistoryException {

		return super.deleteEntry(commentId, MF_DELETE_ONE_ENTRY);

	}

	// ----------------------------------------- private methods
	private DbTestComment buildTComment(ResultSet rs, Map<Integer, DbTestUser> mapId2User) throws JUnitHistoryException {
		try {

			int userId = rs.getInt(DB_USER_ID);
			DbTestUser testUser = null;
			if (mapId2User != null && mapId2User.containsKey(userId)) {
				testUser = mapId2User.get(userId);
			} else {
				// lazy
				testUser = new LazyTestUser(userId);
			}
			long dateCreationts = rs.getLong(DB_DATE_CREATION);
			Date dateCreation = dateCreationts > 0 ? new Date(dateCreationts) : null;

			DbTestComment entry = new DbTestComment(dateCreation, testUser);
			entry.setId(rs.getInt(DB_ID));

			long dateModifts = rs.getLong(DB_DATE_MODIF);
			entry.setDateModification(dateModifts > 0 ? new Date(dateModifts) : null);

			entry.setTitle(this.getValueNotEmpty(rs.getString(DB_TITLE)));
			entry.setDescription(this.getValueNotEmpty(rs.getString(DB_DESC)));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildTComment(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateTComment(DbTestComment entry, int testId, MessageFormat sqlToFormat)
			throws JUnitHistoryException {

		this.verifyComment(entry, testId);

		// String type = this.formatSqlString(entry.getType());
		// String message = this.formatSqlString(entry.getMessage());
		// String stacktrace = this.formatSqlString(entry.getStackTrace());
		// String outputLog = this.formatSqlString(entry.getOutputLog());

		String sql = ""; // sqlToFormat.format(new Object[] { entry.getId(),
							// type, message, stacktrace, outputLog, testId });

		return this.updateOneItem(sql);

	}

	private void verifyComment(DbTestComment entry, int testId) throws JUnitHistoryException {

		String prefix = "DbTestComment";
		super.verifyEntryBeforeSave(prefix, entry);

		// testId defined
		super.verifyIdDefined(prefix + " testId", testId);

		// userId defined
		super.verifyNotNull(prefix + " user", entry.getUser());
		super.verifyIdDefined(prefix + " userId", entry.getUser().getId());

		// title required
		super.verifyNotNull(prefix + " title", entry.getTitle());

		// description required
		super.verifyNotNull(prefix + " description", entry.getDescription());

	}

}
