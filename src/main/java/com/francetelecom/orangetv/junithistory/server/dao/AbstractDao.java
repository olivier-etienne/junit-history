package com.francetelecom.orangetv.junithistory.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.manager.DatabaseManager;
import com.francetelecom.orangetv.junithistory.server.manager.SessionManager.IJUnitHistorySessionListener;
import com.francetelecom.orangetv.junithistory.server.manager.SessionManager.SessionSubscription;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;

public abstract class AbstractDao<T extends IDbEntry> implements IJUnitHistorySessionListener {

	protected static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");

	// token de transaction. Null si autocommit true
	private final String token;

	protected String getToken() {
		return this.token;
	}

	// ---------------------------- constructor
	protected AbstractDao() {
		this(null);
	}

	// Dao pour une transaction specifique
	protected AbstractDao(String token) {
		this.token = token;
		if (this.token != null) {
			getLog().info("addSessionListener(): " + this.token);
			SessionSubscription.get().addSessionListener(this);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		SessionSubscription.get().removeSessionListener(this);
		super.finalize();
	}

	// -------------------------- implementing IJUnitHistorySessionListener
	@Override
	public void sessionCreated(String sessionId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionDestroyed(String sessionId) {

		if (this.token != null && this.token.equals(sessionId)) {
			getLog().info("removeTransactionDao(): " + this.token);
			this.removeTransactionDao(this.token);

			try {
				DatabaseManager.get().rollbackTransaction(this.token);
			} catch (JUnitHistoryException e) {
				getLog().warning(e.getMessage());
			}
		}
	}

	// ----------------------------------- abstract methods
	protected abstract T buildDbEntry(ResultSet rs, Map<String, Object> params) throws JUnitHistoryException;

	protected abstract Logger getLog();

	protected abstract void removeTransactionDao(String token);

	// -------------------------------- protected methods

	protected int getIdFromDbEntry(IDbEntry entry) {

		if (entry == null) {
			return IDbEntry.ID_UNDEFINED;
		}

		return entry.getId();
	}

	/*
	 * Si transaction en cours (token non null) la connection ne doit pas etre
	 * fermee
	 */
	protected void close(ResultSet rs, Statement stmt, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException ignored) {
			// NA
		}

		try {
			if (stmt != null) {
				stmt.close();
			}

		} catch (SQLException ignored) {
			// NA
		}

		// fermeture de la connection dans un contexte non transactionnel
		DatabaseManager.get().closeConnection(con, this.token);
	}

	protected String formatSqlString(String toFormat) {
		if (toFormat != null) {
			toFormat = toFormat.replaceAll("\'", "\'\'");
		}
		return toFormat == null ? "" : toFormat;
	}

	protected int count(String sql) {

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return 0;

	}

	public T getUniqueByValue(String value, MessageFormat sqlPattern) throws JUnitHistoryException {
		return this.getUniqueEntry(this.buildSql(sqlPattern, value));
	}

	/**
	 * Récupère une entrée par son id
	 * 
	 * @param videoId
	 * @return
	 */
	protected T getById(int id, MessageFormat sqlPattern) {
		return this.getById(id, sqlPattern, null);
	}

	protected T getById(int id, MessageFormat sqlPattern, Map<String, Object> params) {
		if (id < 0) {
			return null;
		}

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			String sql = this.buildSql(sqlPattern, id);
			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return this.buildDbEntry(rs, params);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return null;

	}

	protected String buildSql(MessageFormat sqlPattern, int value) {
		return sqlPattern.format(new Object[] { value });
	}

	protected String buildSql(MessageFormat sqlPattern, String... values) {
		return sqlPattern.format(values);
	}

	protected boolean deleteEntry(int id, MessageFormat sqlPattern) throws JUnitHistoryException {

		if (id < 0) {
			return false;
		}

		String sql = sqlPattern.format(new Object[] { id });
		return this.deleteEntries(sql, true);
	}

	protected boolean deleteEntries(String sql) throws JUnitHistoryException {
		return deleteEntries(sql, false);
	}

	private boolean deleteEntries(String sql, boolean oneItem) throws JUnitHistoryException {

		if (sql == null) {
			return false;
		}

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			int count = stmt.executeUpdate(sql);
			return (oneItem) ? (count == 1) : (count > 0);

		} catch (Exception e) {
			throw new JUnitHistoryException("SQLException: " + e.getMessage());
		} finally {
			this.close(rs, stmt, con);
		}

	}

	protected T getUniqueEntry(String sql) throws JUnitHistoryException {

		List<T> list = this.listEntry(sql);
		if (list.size() > 1) {
			throw new JUnitHistoryException("The request returns more than one entry!");
		}
		return (list.isEmpty()) ? null : list.get(0);
	}

	protected Map<Integer, T> getMapId2Entry(String sql) throws JUnitHistoryException {

		List<T> list = this.listEntry(sql);
		return VoIdUtils.getMapId2Item(list);
	}

	protected List<T> listEntry(String sql) throws JUnitHistoryException {
		return this.listEntry(sql, null);
	}

	protected List<T> listEntry(String sql, Map<String, Object> params) throws JUnitHistoryException {

		List<T> list = new ArrayList<T>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				T entry = this.buildDbEntry(rs, params);
				if (entry != null) {
					list.add(entry);
				}
			}

		} catch (SQLException ex) {
			throw new JUnitHistoryException("SQLException: " + ex.getMessage());
		} finally {
			this.close(rs, stmt, con);
		}

		return list;
	}

	protected List<String> listStringAttribute(String sql, String key) throws JUnitHistoryException {

		List<String> list = new ArrayList<>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				String name = rs.getString(key);
				if (name != null) {
					list.add(name);
				}
			}

		} catch (SQLException ex) {
			throw new JUnitHistoryException("SQLException: " + ex.getMessage());
		} finally {
			this.close(rs, stmt, con);
		}

		return list;
	}

	/**
	 * permet de traiter une requete de type select id, count(id) from table
	 * group by id pour n'importe quelle type de table et d'id
	 * 
	 * @return map [id, count(id)]
	 */
	protected Map<Integer, Integer> buildMapId2Value(String sql) throws JUnitHistoryException {

		Map<Integer, Integer> map = new HashMap<>();

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.getToken());
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);

			while (rs.next()) {

				int id = rs.getInt(1);
				int count = rs.getInt(2);
				map.put(id, count);

			}
		} catch (SQLException ex) {
			throw new JUnitHistoryException("SQLException: " + ex.getMessage());
		} finally {
			this.close(rs, stmt, con);
		}

		return map;
	}

	protected int getMaxId(String sql) {

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			getLog().fine("sql: " + sql);
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 10;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.close(rs, stmt, con);
		}

		return 0;

	}

	/**
	 * Update d'un seul element
	 * 
	 * @param sql
	 * @return
	 * @throws JUnitHistoryException
	 */
	protected boolean updateOneItem(String sql) throws JUnitHistoryException {

		getLog().fine("update sql: " + sql);

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {

			con = DatabaseManager.get().getConnection(this.token);
			stmt = con.createStatement();

			int result = stmt.executeUpdate(sql);
			return result == 1;

		} catch (SQLException ex) {
			throw new JUnitHistoryException("SQLException: " + ex.getMessage());
		}

		finally {
			this.close(rs, stmt, con);

		}

	}

	protected String getValueNotEmpty(String value) {
		return ValueHelper.isStringEmptyOrNull(value) ? null : value;
	}

	protected void verifyNotNull(String comment, Object value) throws JUnitHistoryException {
		if (value == null) {
			throw new JUnitHistoryException(comment + " is required!");
		}
	}

	/**
	 * Récupère une entrée par son id sur la list cached
	 * 
	 * @param entryId
	 *            , List<T> cachedList
	 * @return
	 */
	protected T getCachedById(int entryId, List<T> cachedList) throws JUnitHistoryException {

		if (cachedList == null) {
			return null;
		}
		for (T entry : cachedList) {
			if (entry.getId() == entryId) {
				return entry;
			}
		}
		return null;

	}

	protected void verifyIdForCreateEntry(AbstractDbEntry entry, String sqlCountMaxId) {

		if (entry != null && entry.getId() == IDbEntry.ID_UNDEFINED) {
			int maxId = getMaxId(sqlCountMaxId);
			entry.setId(maxId + 1);
		}
	}

	protected void verifyIdForUpdateEntry(AbstractDbEntry entry) throws JUnitHistoryException {
		if (entry.getId() == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException("Id must be defined for update!");
		}
	}

	protected void verifyEntryBeforeSave(String comment, T entry) throws JUnitHistoryException {

		this.verifyNotNull(comment, entry);
		if (entry.getId() == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException(comment + ": id must be defined!");
		}

	}

}