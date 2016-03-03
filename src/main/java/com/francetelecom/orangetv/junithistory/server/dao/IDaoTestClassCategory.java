package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestClassCategory extends IDao {

	public static final char CLASSNAME_SEPARATOR = ',';

	public static final String C = "c";
	public static final String TABLE_NAME = "clcategory";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + C + " ";

	public static final String DB_NAME = "name";
	public static final String DB_SUITE_NAMES = "suitenames";
	public static final String DB_CAT_DEFAULT = "catDefault";

	public static final String CP = C + ".";

	// =============================================
	// SIMPLE CLASS CATEGORY
	// =============================================
	public final static String CATEGORY_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_SUITE_NAMES + ", " // ...
			+ DB_CAT_DEFAULT + " "; // ...

	public final static String SQL_SELECT_CATEGORY = SELECT + CATEGORY_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_CATEGORY + WHERE_ONE_ENTRY);

	public final static String SQL_DELETE_ALL = DELETE + FROM + TABLE_NAME + " " + WHERE + "1";

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_CATEGORY = new MessageFormat(INSERT + TABLE_NAME + " ("
			+ CATEGORY_ATTRIBUTS + ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ "''{1}'', " // name ...
			+ "''{2}'', " // suitenames...
			+ "{3} " // catdefault...
			+ ");"); // ...

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

	public final static MessageFormat MF_SELECT_BY_NAME = new MessageFormat(SQL_SELECT_CATEGORY + WHERE + DB_NAME
			+ " = ''{0}''");

	// =============================================
	// UPDATE CATEGORY
	// =============================================
	public final static MessageFormat MF_UPDATE_CATEGORY = new MessageFormat(UPDATE + TABLE_NAME + " " + SET // ...
			+ DB_NAME + " = ''{0}'', " // ...
			+ DB_SUITE_NAMES + " = ''{1}'' " // ...
			+ WHERE + DB_ID + " = {2, number, ####} ");

	// =============================================
	// COUNT CATEGORY
	// =============================================
	public final static String SQL_COUNT_CATEGORIES = SELECT + COUNT_ALL + FROM + TABLE_NAME + " ";
	public final static MessageFormat MF_COUNT_BY_NAME = new MessageFormat(SQL_COUNT_CATEGORIES + WHERE + DB_NAME
			+ " = ''{0}''");

}
