package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;

public class DaoTestClassCategory extends AbstractDao<DbTestClassCategory> implements IDaoTestClassCategory {

	private static final Logger log = Logger.getLogger(DaoTestClassCategory.class.getName());

	private static boolean initDone = false;
	// ---------------------------------- Singleton
	private static DaoTestClassCategory instance;
	private static final Map<String, DaoTestClassCategory> mapToken2Dao = Collections
			.synchronizedMap(new HashMap<String, DaoTestClassCategory>());

	public static final DaoTestClassCategory get() {
		if (instance == null) {
			instance = new DaoTestClassCategory();
		}
		return instance;
	}

	public static final DaoTestClassCategory getWithTransaction(String token) {
		if (token == null) {
			return get();
		}
		if (mapToken2Dao.containsKey(token)) {
			return mapToken2Dao.get(token);
		}
		DaoTestClassCategory dao = new DaoTestClassCategory(token);
		mapToken2Dao.put(token, dao);

		return dao;
	}

	private DaoTestClassCategory() {
		this(null);
	}

	private DaoTestClassCategory(String token) {
		super(token);
		this.init();
	}

	private void init() {
		if (initDone) {
			return;
		}
		// on s'assure que l'utilisateur admin exist sinon on le cree
		try {
			if (this.getDefaultCategory(false) == null) {

				DbTestClassCategory defaultCategory = new DbTestClassCategory("other");
				defaultCategory.setDefaultValue(true);
				if (this.createCategory(defaultCategory)) {
					initDone = true;
				} else {
					throw new RuntimeException("Failure when creating default category!");
				}
			}
		} catch (JUnitHistoryException e) {
			log.severe("Unable to get or create default category !");
			throw new RuntimeException("DaoTestClassCategory cannot get or create default category!");
		}

	}

	// --------------------------------------------- attributs
	private DbTestClassCategory cachedDefaultClassCategory;
	private List<DbTestClassCategory> cachedListCategories;

	// ---------------------------------------- overriding AbstractDao
	@Override
	protected void removeTransactionDao(String token) {
		mapToken2Dao.remove(token);
	}

	@Override
	protected DbTestClassCategory buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException {
		return this.buildCategory(rs);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------------------- public methods
	/*
	 * Clear cache in all Dao instance
	 */
	public void reinitAllDaoCaches() {
		for (DaoTestClassCategory dao : mapToken2Dao.values()) {
			dao.clearCaches();
		}
	}

	public void deleteAll() throws JUnitHistoryException {

		super.updateOneItem(SQL_DELETE_ALL);
		this.clearCaches();
	}

	public DbTestClassCategory getDefaultCategory(boolean cached) throws JUnitHistoryException {

		if (cached && this.cachedDefaultClassCategory != null) {
			return this.cachedDefaultClassCategory;
		}

		List<DbTestClassCategory> list = this.listCategories(cached);
		for (DbTestClassCategory dbTestClassCategory : list) {
			if (dbTestClassCategory.isDefaultValue()) {
				this.cachedDefaultClassCategory = dbTestClassCategory;
				break;
			}
		}

		return this.cachedDefaultClassCategory;
	}

	public List<DbTestClassCategory> listCategories(boolean cached) throws JUnitHistoryException {

		if (cached && this.cachedListCategories != null) {
			return this.cachedListCategories;
		}

		this.cachedListCategories = super.listEntry(SQL_SELECT_CATEGORY);
		return this.cachedListCategories;
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param categoryId
	 * @return
	 */
	public DbTestClassCategory getById(int categoryId, boolean cached) throws JUnitHistoryException {

		if (cached) {
			return this.getCachedById(categoryId);
		}
		return super.getById(categoryId, MF_SELECT_ONE_ENTRY);

	}

	public int countByName(String name) throws JUnitHistoryException {
		return super.count(MF_COUNT_BY_NAME.format(new String[] { name }));
	}

	/**
	 * Récupère une entrée par son name
	 * 
	 * @param name
	 * @return
	 */
	public DbTestClassCategory getByName(String name) throws JUnitHistoryException {

		return super.getUniqueByValue(name, MF_SELECT_BY_NAME);

	}

	/**
	 * Create a new TestSuiteInstance
	 * 
	 * @param testInstance
	 * @return
	 */
	public boolean createCategory(DbTestClassCategory category) throws JUnitHistoryException {

		super.verifyIdForCreateEntry(category, SQL_SELECT_MAX_ID);
		boolean result = this.createOrUpdateCategory(category, MF_CREATE_CATEGORY, true);
		this.clearCaches();
		return result;
	}

	/**
	 * Update an existing category
	 */
	public boolean updateCategory(DbTestClassCategory category) throws JUnitHistoryException {
		this.verifyIdForUpdateEntry(category);
		boolean result = this.createOrUpdateCategory(category, MF_UPDATE_CATEGORY, false);
		this.clearCaches();
		return result;
	}

	public boolean deleteCategory(int categoryId) throws JUnitHistoryException {

		boolean result = super.deleteEntry(categoryId, MF_DELETE_ONE_ENTRY);
		this.clearCaches();
		return result;

	}

	// -------------------------------------- private method

	private String[] getClassNamesAsTab(String suitenames) {

		List<String> list = ObjectUtils.buildListItems(suitenames, CLASSNAME_SEPARATOR + "");
		return ObjectUtils.listToTab(list);
	}

	/**
	 * Récupère une entrée par son id sur la list cached
	 * 
	 * @param categoryId
	 * @return
	 */
	private DbTestClassCategory getCachedById(int categoryId) throws JUnitHistoryException {

		return super.getCachedById(categoryId, this.listCategories(true));

	}

	private DbTestClassCategory buildCategory(ResultSet rs) throws JUnitHistoryException {
		try {

			DbTestClassCategory entry = new DbTestClassCategory(rs.getString(DB_NAME));
			entry.setId(rs.getInt(DB_ID));

			String suitenames = rs.getString(DB_SUITE_NAMES);
			entry.setSuiteNames(this.getClassNamesAsTab(suitenames));
			entry.setDefaultValue(rs.getBoolean(DB_CAT_DEFAULT));

			return entry;
		} catch (Exception e) {
			throw new JUnitHistoryException("Error in buildCategory(): " + e.getMessage());
		}

	}

	private boolean createOrUpdateCategory(DbTestClassCategory entry, MessageFormat sqlToFormat, boolean created)
			throws JUnitHistoryException {

		this.verifyCategory(entry);

		String name = this.formatSqlString(entry.getName());
		String suitenames = this.formatSqlString(ObjectUtils.tabToString(entry.getSuiteNames(), CLASSNAME_SEPARATOR));

		String sql = created ? sqlToFormat.format(new Object[] { entry.getId(), name, suitenames,
				entry.isDefaultValue() }) : sqlToFormat.format(new Object[] { name, suitenames, entry.getId() });

		return this.updateOneItem(sql);

	}

	private void verifyCategory(DbTestClassCategory category) throws JUnitHistoryException {

		String prefix = "DbTestClassCategory";
		super.verifyEntryBeforeSave(prefix, category);

		// name required
		super.verifyNotNull(prefix + " name", category.getName());

		// suitenames required sauf defaut category
		if (!category.isDefaultValue()) {
			super.verifyNotNull(prefix + " suitenames", category.getSuiteNames());
		}

	}

	private void clearCaches() {
		this.cachedDefaultClassCategory = null;
		this.cachedListCategories = null;
	}

}
