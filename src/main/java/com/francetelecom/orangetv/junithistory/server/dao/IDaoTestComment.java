package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestComment extends IDao {

	public static final String O = "o";
	public static final String OP = O + P;

	public static final String TABLE_NAME = "tcomment";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + O + " ";

	public static final String DB_TITLE = "title";
	public static final String DB_DESC = "description";

	public static final String DB_TEST_ID = "testId";
	public static final String DB_USER_ID = "userId";

	public static final String DB_DATE_CREATION = "date_creation";
	public static final String DB_DATE_MODIF = "date_modif";

	// =============================================
	// SIMPLE TEST COMMENT
	// =============================================
	public final static String TCOMMENT_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_TITLE + ", " // ...
			+ DB_DESC + ", " // ...
			+ DB_TEST_ID + ", " // ...
			+ DB_USER_ID + ", " // ...
			+ DB_DATE_CREATION + ", " // ...
			+ DB_DATE_MODIF + " "; // ...

	public final static String SQL_SELECT_TCOMMENT = SELECT + TCOMMENT_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_TCOMMENT + WHERE_ONE_ENTRY);

	public final static MessageFormat MF_SELECT_FOR_TEST = new MessageFormat(SQL_SELECT_TCOMMENT + WHERE + DB_TEST_ID
			+ EGAL_NUMBER);

	// =============================================
	// DIVERS
	// =============================================
	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_COMMENT = new MessageFormat(INSERT + TABLE_NAME + " ("
			+ TCOMMENT_ATTRIBUTS + ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " ''{1}'', " // title...
			+ "''{2}'', " // description ...
			+ "{3, number, ####} , " // testId ...
			+ "{4, number, ####} ," // userId...
			+ "{5} " // date creation...
			+ "{6} " // date modification...
			+ ");"); // ...

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

}
