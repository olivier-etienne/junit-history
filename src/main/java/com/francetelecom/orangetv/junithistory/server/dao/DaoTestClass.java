package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

/**
 * Dao for DbTestClass management
 * 
 * @author ndmz2720
 * 
 */
public class DaoTestClass extends AbstractDao<DbTestClass> implements IDaoTestClass {

	private static final Logger log = Logger.getLogger(DaoTestClass.class.getName());

	// ---------------------------------- Singleton
	private static DaoTestClass instance;
	private static final Map<String, DaoTestClass> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestClass>());

	public static final DaoTestClass get() {
		if (instance == null) {
			instance = new DaoTestClass();
		}
		return instance;
	}

	public static final DaoTestClass getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestClass dao = new DaoTestClass(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestClass() {
		this(null);
	}

	private DaoTestClass(String token) {
		super(token);
	}

	// ---------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbTestClass buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {
		return this.buildTClass(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods
	/**
	 * 
	 * @return map [categoryId, count(categoryId)]
	 */
	public Map<Integer, Integer> countCategories() throws JUnitHistoryException {

		return super.buildMapId2Value(COUNT_CATEGORIES);
	}

	/**
	 * Retourne le nombre de tclass utilisant cette category
	 * 
	 * @param categoryId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public int countByCategory(int categoryId) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_CATEGORY.format(new Integer[] { categoryId }));
	}

	public void deleteAll() throws JUnitHistoryException {

		super.updateOneItem(SQL_DELETE_ALL);
	}

	public Map<Integer, DbTestClass> getMapId2TClass() throws JUnitHistoryException {

		return super.getMapId2Entry(SQL_SELECT_TCLASS);

	}

	public List<DbTestClass> listTClasses() throws JUnitHistoryException {

		return super.listEntry(SQL_SELECT_TCLASS);
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param tclassId
	 * @return
	 */
	public DbTestClass getById(int tclassId) throws JUnitHistoryException {

		return super.getById(tclassId, MF_SELECT_ONE_ENTRY);

	}

	/*
	 * Modifie la category d'une tclass
	 */
	public boolean updateCategoryId(int tclassId, int categoryId) throws JUnitHistoryException {

		String sql = MF_UPDATE_CATEGORY_ID.format(new Object[] { categoryId, tclassId });
		return this.updateOneItem(sql);
	}

	/**
	 * Récupère une entrée par son name
	 * 
	 * @param tclassId
	 * @return
	 */
	public DbTestClass getByName(String className) throws JUnitHistoryException {

		return super.getUniqueByValue(className, MF_SELECT_BY_NAME);

	}

	/**
	 * Create a new TestClass
	 * 
	 * @param tclass
	 * @return id
	 */
	public boolean createTClass(DbTestClass tclass) throws JUnitHistoryException {

		if (tclass == null) {
			return false;
		}
		super.verifyIdForCreateEntry(tclass, SQL_SELECT_MAX_ID);

		return this.createOrUpdateTClass(tclass, MF_CREATE_TCLASS);
	}

	public boolean deleteTClass(int tclassId) throws JUnitHistoryException {

		return super.deleteEntry(tclassId, MF_DELETE_ONE_ENTRY);

	}

	// -------------------------------------- private method
	private DbTestClass buildTClass(ResultSet rs) throws JUnitHistoryException {
		try {

			DbTestClass entry = new DbTestClass(rs.getString(DB_NAME));
			entry.setId(rs.getInt(DB_ID));

			int categoryId = rs.getInt(DB_CATEGORY_ID);
			DbTestClassCategory category = DaoTestClassCategory.get().getById(categoryId, true);
			entry.setCategory(category);

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildTClass(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateTClass(DbTestClass entry, MessageFormat sqlToFormat) throws JUnitHistoryException {

		this.verifyTClass(entry);

		String name = this.formatSqlString(entry.getName());
		int categoryId = super.getIdFromDbEntry(entry.getCategory());

		String sql = sqlToFormat.format(new Object[] { entry.getId(), name, categoryId });

		return this.updateOneItem(sql);

	}

	private boolean verifyTClass(DbTestClass entry) throws JUnitHistoryException {

		String prefix = "DbTestClass";
		super.verifyEntryBeforeSave(prefix, entry);

		// Name required
		this.verifyNotNull(prefix + " name", entry.getName());
		// Category required
		this.verifyNotNull(prefix + " category", entry.getCategory());
		return true;
	}

}
