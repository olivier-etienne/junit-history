package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;

public class DaoTestInstance extends AbstractDao<DbTestInstance> implements IDaoTestInstance {

	private static final Logger log = Logger.getLogger(DaoTestInstance.class.getName());

	private static final String PARAM_TCLASS_LIST = "paramTClassList";
	private static final String PARAM_MESS_LIST = "paramMessList";
	private static final String PARAM_MESS = "paramMess";
	// ---------------------------------- Singleton
	private static DaoTestInstance instance;
	private static final Map<String, DaoTestInstance> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestInstance>());

	public static final DaoTestInstance get() {
		if (instance == null) {
			instance = new DaoTestInstance();
		}
		return instance;
	}

	public static final DaoTestInstance getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestInstance dao = new DaoTestInstance(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestInstance() {
		this(null);
	}

	private DaoTestInstance(String token) {
		super(token);
	}

	// ----------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected DbTestInstance buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {

		final Map<Integer, DbTestClass> mapId2TClass = (params != null && params.containsKey(PARAM_TCLASS_LIST)) ? (Map<Integer, DbTestClass>) params
				.get(PARAM_TCLASS_LIST) : null;

		Map<Integer, DbTestMessage> mapId2Message = (params != null && params.containsKey(PARAM_MESS_LIST)) ? (Map<Integer, DbTestMessage>) params
				.get(PARAM_MESS_LIST) : null;

		final DbTestMessage message = (params != null && params.containsKey(PARAM_MESS)) ? (DbTestMessage) params
				.get(PARAM_MESS) : null;

		if (mapId2Message == null && message != null) {
			mapId2Message = ObjectUtils.buildMapIdWithOneItem(message.getId(), message);
		}

		return this.buildTest(rs, mapId2TClass, mapId2Message);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods
	public int count() throws JUnitHistoryException {
		return super.count(SQL_COUNT_TEST);
	}

	public int countForSuite(int suiteId) throws JUnitHistoryException {
		return super.count(MF_COUNT_TEST_FOR_SUITE.format(new Integer[] { suiteId }));
	}

	public List<DbTestInstance> listTests() throws JUnitHistoryException {

		if (this.count() == 0) {
			return new ArrayList<>(0);
		}

		return super.listEntry(SQL_SELECT_TEST_JOIN_MESS, this.buildMapParams());
	}

	/**
	 * 
	 * @param suiteId
	 * @param lazyMessage
	 *            ref sur l'objet message
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<DbTestInstance> listTestsForSuite(int suiteId, boolean lazyMessage) throws JUnitHistoryException {

		if (this.countForSuite(suiteId) == 0) {
			return new ArrayList<>(0);
		}

		Map<String, Object> map = this.buildMapParams();
		if (!lazyMessage) {
			final List<DbTestMessage> listMessageForSuite = DaoTestMessage.get().listMessagesForSuite(suiteId);
			final Map<Integer, DbTestMessage> mapId2Message = VoIdUtils.getMapId2Item(listMessageForSuite);
			map.put(PARAM_MESS_LIST, mapId2Message);
		}

		return super.listEntry(MF_SELECT_TEST_JOIN_MESS_FOR_SUITE.format(new Integer[] { suiteId }), map);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param testId
	 * @return Test avec son message complet si existe
	 */
	public DbTestInstance getById(int testId) throws JUnitHistoryException {

		DbTestMessage message = DaoTestMessage.get().getByTest(testId);

		return super.getById(testId, MF_SELECT_ONE_ENTRY_JOIN_MESS,
				ObjectUtils.buildMapWithOneItem(PARAM_MESS, message));

	}

	/**
	 * Create a new TestSuiteInstance
	 * 
	 * @param testInstance
	 * @return
	 */
	public boolean createTest(DbTestInstance testInstance) throws JUnitHistoryException {

		if (testInstance == null) {
			return false;
		}
		super.verifyIdForCreateEntry(testInstance, SQL_SELECT_MAX_ID);
		return this.createOrUpdateTest(testInstance, MF_CREATE_TEST);
	}

	public boolean deleteTest(int testId) throws JUnitHistoryException {

		return super.deleteEntry(testId, MF_DELETE_ONE_ENTRY);

	}

	public boolean deleteTestsForTestSuite(int suiteId) throws JUnitHistoryException {

		String sql = MF_DELETE_FOR_SUITE.format(new Integer[] { suiteId });
		return this.deleteEntries(sql);

	}

	// -------------------------------------- private method

	/*
	 * Attention le message peut etre lazy!
	 */
	private DbTestInstance buildTest(ResultSet rs, Map<Integer, DbTestClass> mapId2TClass,
			Map<Integer, DbTestMessage> mapId2Message) throws JUnitHistoryException {
		try {
			// test suite
			DbTestSuiteInstance testSuiteInstance = new LazyTestSuiteInstance(rs.getInt(DB_SUITE_ID));

			// test class
			int tclassId = rs.getInt(DB_TCLASS_ID);
			DbTestClass testClass = (mapId2TClass != null) ? mapId2TClass.get(tclassId) : DaoTestClass
					.getWithTransaction(this.getToken()).getById(tclassId);

			DbTestInstance entry = new DbTestInstance(testSuiteInstance, testClass);

			entry.setId(rs.getInt(DB_ID));
			entry.setName(rs.getString(DB_NAME));

			String status = rs.getString(DB_STATUS);
			entry.setStatus(TestSubStatusEnum.valueOf(status));
			entry.setTime(rs.getLong(DB_TIME));

			if (entry.getStatus() != TestSubStatusEnum.success) {
				entry.setMessage(new LazyTestMessage(entry.getId()));
			}

			int messageId = rs.getInt(MESS_ID);
			if (messageId > 0) {
				DbTestMessage message = mapId2Message == null ? new LazyTestMessage(messageId) : mapId2Message
						.get(messageId);
				entry.setMessage(message);
			}

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildDbTestSuiteInstance(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateTest(DbTestInstance test, MessageFormat sqlToFormat) throws JUnitHistoryException {

		this.verifyTest(test);

		String name = this.formatSqlString(test.getName());
		String status = test.getStatus().name();

		int suiteId = test.getTestSuiteInstance().getId();
		int tclassId = test.gettClass().getId();

		String sql = sqlToFormat.format(new Object[] { test.getId(), name, status, suiteId, test.getTime(), tclassId });

		return this.updateOneItem(sql);

	}

	private boolean verifyTest(DbTestInstance entry) throws JUnitHistoryException {

		final String prefix = "DbTestInstance.";
		super.verifyEntryBeforeSave(prefix, entry);

		// Name required
		this.verifyNotNull(prefix + "name", entry.getName());
		// Status required
		this.verifyNotNull(prefix + "status", entry.getStatus());
		// TestSuite required
		this.verifyNotNull(prefix + "testSuite", entry.getTestSuiteInstance());
		// TestClass required
		this.verifyNotNull(prefix + "testClass", entry.gettClass());

		return true;
	}

	private Map<String, Object> buildMapParams() throws JUnitHistoryException {
		// Aggregation with DbTestClass
		Map<Integer, DbTestClass> mapId2TClass = DaoTestClass.getWithTransaction(this.getToken()).getMapId2TClass();
		return ObjectUtils.buildMapWithOneItem(PARAM_TCLASS_LIST, mapId2TClass);
	}

}
