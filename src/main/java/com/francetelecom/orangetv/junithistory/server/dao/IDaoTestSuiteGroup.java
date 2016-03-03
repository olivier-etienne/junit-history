package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestSuiteGroup extends IDao {

	public static final String G = "g";
	public static final String TABLE_NAME = "suitegroup";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + G + " ";

	public static final String DB_STB = "stb";
	public static final String DB_NAME = "name";
	public static final String DB_PREFIX = "prefix";

	// =============================================
	// SIMPLE TEST GROUP
	// =============================================
	public final static String GROUP_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_STB + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_PREFIX + " "; // ...

	public final static String SQL_SELECT_GROUP = SELECT + GROUP_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_GROUP + WHERE_ONE_ENTRY);

	public final static String SQL_DELETE_ALL = DELETE + FROM + TABLE_NAME + " " + WHERE + "1";

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_GROUP = new MessageFormat(INSERT + TABLE_NAME + " (" + GROUP_ATTRIBUTS
			+ ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " ''{1}'', " // stb...
			+ "''{2}'', " // name...
			+ "''{3}'' " // prefix...
			+ ");"); // ...

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

	// =============================================
	// UPDATE GROUP
	// =============================================
	public final static MessageFormat MF_UPDATE_GROUP = new MessageFormat(UPDATE + TABLE_NAME + " " + SET // ...
			+ DB_STB + " = ''{0}'', " // ...
			+ DB_NAME + " = ''{1}'', " // ...
			+ DB_PREFIX + " = ''{2}'' " // ...
			+ WHERE + DB_ID + " = {3, number, ####} ");

	// =============================================
	// COUNT GROUPS
	// =============================================
	public final static String SQL_COUNT_GROUPS = SELECT + COUNT_ALL + FROM + TABLE_NAME + " ";
	public final static MessageFormat MF_COUNT_BY_NAME = new MessageFormat(SQL_COUNT_GROUPS + WHERE + DB_NAME
			+ " = ''{0}''");

}
