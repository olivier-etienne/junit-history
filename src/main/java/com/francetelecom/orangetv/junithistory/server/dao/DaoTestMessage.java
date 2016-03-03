package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class DaoTestMessage extends AbstractDao<DbTestMessage> implements IDaoTestMessage {

	private static final Logger log = Logger.getLogger(DaoTestMessage.class.getName());

	// ---------------------------------- Singleton
	private static DaoTestMessage instance;
	private static final Map<String, DaoTestMessage> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestMessage>());

	public static final DaoTestMessage get() {
		if (instance == null) {
			instance = new DaoTestMessage();
		}
		return instance;
	}

	public static final DaoTestMessage getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestMessage dao = new DaoTestMessage(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestMessage() {
		this(null);
	}

	private DaoTestMessage(String token) {
		super(token);
	}

	// ----------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbTestMessage buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {

		return this.buildMessage(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods
	public List<DbTestMessage> listMessages() throws JUnitHistoryException {

		return super.listEntry(SQL_SELECT_MESSAGE);
	}

	public List<DbTestMessage> listMessagesForSuite(int suiteId) throws JUnitHistoryException {

		return super.listEntry(MF_SELECT_JOIN_TEST_FOR_SUITE.format(new Integer[] { suiteId }));
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	public DbTestMessage getById(int messageId) throws JUnitHistoryException {

		return super.getById(messageId, MF_SELECT_ONE_ENTRY);

	}

	public DbTestMessage getByTest(int testId) throws JUnitHistoryException {
		return super.getUniqueEntry(MF_SELECT_FOR_TEST.format(new Integer[] { testId }));
	}

	/**
	 * Create a new TestMessage
	 * 
	 * @param testInstance
	 * @return
	 */
	public boolean createMessage(DbTestMessage testMessage, int testId) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(testMessage, SQL_SELECT_MAX_ID);

		// testId required
		if (testId == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException("testId must be defined in message!");
		}
		return this.createOrUpdateMessage(testMessage, testId, MF_CREATE_MESSAGE);
	}

	public boolean deleteMessage(int messageId) throws JUnitHistoryException {

		return super.deleteEntry(messageId, MF_DELETE_ONE_ENTRY);

	}

	// ----------------------------------------- private methods
	private DbTestMessage buildMessage(ResultSet rs) throws JUnitHistoryException {
		try {

			DbTestMessage entry = new DbTestMessage(rs.getString(DB_TYPE));
			entry.setId(rs.getInt(DB_ID));
			entry.setMessage(this.getValueNotEmpty(rs.getString(DB_MESSAGE)));
			entry.setStackTrace(this.getValueNotEmpty(rs.getString(DB_STACK_TRACE)));
			entry.setOutputLog(this.getValueNotEmpty(rs.getString(DB_OUPUT_LOG)));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildMessage(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateMessage(DbTestMessage entry, int testId, MessageFormat sqlToFormat)
			throws JUnitHistoryException {

		this.verifyMessage(entry);

		String type = this.formatSqlString(entry.getType());
		String message = this.formatSqlString(entry.getMessage());
		String stacktrace = this.formatSqlString(entry.getStackTrace());
		String outputLog = this.formatSqlString(entry.getOutputLog());

		String sql = sqlToFormat.format(new Object[] { entry.getId(), type, message, stacktrace, outputLog, testId });

		return this.updateOneItem(sql);

	}

	private void verifyMessage(DbTestMessage entry) throws JUnitHistoryException {

		String prefix = "DbTestMessage";
		super.verifyEntryBeforeSave(prefix, entry);

		// type required
		super.verifyNotNull(prefix + " type", entry.getType());

		// message required
		super.verifyNotNull(prefix + " message", entry.getMessage());

	}

}
