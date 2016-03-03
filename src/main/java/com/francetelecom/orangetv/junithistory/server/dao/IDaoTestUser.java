package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestUser extends IDao {

	public static final String U = "u";
	public static final String UP = U + P;
	public static final String TABLE_NAME = "tuser";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + U + " ";

	public static final String DB_NAME = "name";
	public static final String DB_DESCR = "description";
	public static final String DB_ADMIN = "admin";

	// =============================================
	// SIMPLE TEST USER
	// =============================================
	public final static String USER_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_DESCR + ", " // ...
			+ DB_ADMIN + " "; // ...

	public final static String SQL_SELECT_USER = SELECT + USER_ATTRIBUTS + FROM + TABLE_NAME + " ";
	public final static String SQL_SELECT_USER_NO_ADMIN = SQL_SELECT_USER + WHERE + DB_ADMIN + " = 0 ";

	public final static String SQL_SELECT_USER_ADMIN = SQL_SELECT_USER + WHERE + DB_ADMIN + " = 1 ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_USER + WHERE_ONE_ENTRY);

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_USER = new MessageFormat(INSERT + TABLE_NAME + " (" + USER_ATTRIBUTS
			+ ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " ''{1}'', " // name...
			+ "''{2}'', " // description...
			+ " {3} " // admin...
			+ ");"); // ...

	public final static String SQL_COUNT_TEST = COUNT_ALL + FROM + TABLE_NAME;
	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

	// =============================================
	// UPDATE USER
	// =============================================
	public final static MessageFormat MF_UPDATE_USER = new MessageFormat(UPDATE + TABLE_NAME + " " + SET // ...
			+ DB_NAME + " = ''{0}'', " // ...
			+ DB_DESCR + " = ''{1}'' " // ...
			+ WHERE + DB_ID + " = {2, number, ####} ");

	// =============================================
	// COUNT USER
	// =============================================
	public final static String SQL_COUNT_USERS = SELECT + COUNT_ALL + FROM + TABLE_NAME + " ";
	public final static MessageFormat MF_COUNT_BY_NAME = new MessageFormat(SQL_COUNT_USERS + WHERE + DB_NAME
			+ " = ''{0}''");

}
