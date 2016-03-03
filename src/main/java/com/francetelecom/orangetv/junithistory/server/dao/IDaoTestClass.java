package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestClass extends IDao {

	public static final String E = "e";
	public static final String TABLE_NAME = "tclass";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + E + " ";

	public static final String DB_NAME = "name";
	public static final String DB_CATEGORY_ID = "categoryId";

	public static final String EP = E + ".";
	// =============================================
	// SIMPLE TEST CLASS
	// =============================================
	public final static String TCLASS_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_CATEGORY_ID + " "; // ...

	public final static String SQL_SELECT_TCLASS = SELECT + TCLASS_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_TCLASS + WHERE_ONE_ENTRY);

	public final static MessageFormat MF_SELECT_BY_NAME = new MessageFormat(SQL_SELECT_TCLASS + WHERE + DB_NAME
			+ " = ''{0}''");

	public final static String SQL_DELETE_ALL = DELETE + FROM + TABLE_NAME + " " + WHERE + "1";

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_TCLASS = new MessageFormat(INSERT + TABLE_NAME + " ("
			+ TCLASS_ATTRIBUTS + ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ "''{1}'', " // name ...
			+ "{2, number, ####} " // categoryId...
			+ ");"); // ...

	// =============================================
	// COUNT
	// =============================================
	public final static String SQL_COUNT_TCLASS = SELECT + COUNT_ALL + FROM + TABLE_NAME + " ";

	// SELECT categoryId, count(categoryId) FROM `tclass` group by
	// categoryId
	public final static String COUNT_CATEGORIES = SELECT + DB_CATEGORY_ID + ", " + COUNT + "(" + DB_CATEGORY_ID + ") "
			+ FROM + TABLE_NAME + " " + GROUP_BY + DB_CATEGORY_ID;

	public final static MessageFormat MF_COUNT_BY_CATEGORY = new MessageFormat(SQL_COUNT_TCLASS + WHERE
			+ DB_CATEGORY_ID + " = {0, number, ####}");

	// =============================================
	// UPDATE categoryId
	// =============================================
	public final static MessageFormat MF_UPDATE_CATEGORY_ID = new MessageFormat(UPDATE + TABLE_NAME + " " + SET // ...
			+ DB_CATEGORY_ID + " = {0, number, ####}  " // ...
			+ WHERE + DB_ID + " = {1, number, ####} ");

	// ===================================
	// DIVERS
	// ===================================

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

}
