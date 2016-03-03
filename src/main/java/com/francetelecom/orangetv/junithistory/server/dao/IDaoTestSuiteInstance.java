package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestSuiteInstance extends IDao {

	public static final String S = "s";
	public static final String SP = S + P;
	public static final String TABLE_NAME = "suite";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + S + " ";

	public static final String DB_NAME = "name";
	public static final String DB_FIRMWARE = "firmware";
	public static final String DB_IPTVKIT = "iptvkit";
	public static final String DB_COMMENT = "comment";

	public static final String DB_DATE = "date";
	public static final String DB_LOG = "log";
	public static final String DB_TIME = "time";

	public static final String DB_GROUP_ID = "groupId";
	public static final String DB_USER_ID = "userId";

	public static final String DB_READ_ONLY = "readonly";

	// =============================================
	// SIMPLE TEST SUITE INSTANCE
	// =============================================
	public final static String SUITE_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_FIRMWARE + ", " // ...
			+ DB_IPTVKIT + ", " // ...
			+ DB_COMMENT + ", " // ...
			+ DB_DATE + ", " // ...
			+ DB_LOG + ", " // ...
			+ DB_TIME + ", " // ...
			+ DB_GROUP_ID + ", " // ...
			+ DB_USER_ID + ", " // ...
			+ DB_READ_ONLY + " "; // ...

	public final static String SQL_SELECT_SUITE = SELECT + SUITE_ATTRIBUTS
			+ FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(
			SQL_SELECT_SUITE + WHERE_ONE_ENTRY);

	public final static MessageFormat MF_SELECT_BY_NAME = new MessageFormat(
			SQL_SELECT_SUITE + WHERE + DB_NAME + " = ''{0}''");

	public final static MessageFormat MF_SELECT_BY_GROUP = new MessageFormat(
			SQL_SELECT_SUITE + WHERE + DB_GROUP_ID + " = {0, number, ####} "
					+ ORDER_BY + DB_DATE + " " + DESC + ", " + DB_NAME + " "
					+ DESC);

	public final static MessageFormat MF_CREATE_SUITE = new MessageFormat(
			INSERT + TABLE_NAME + " (" + SUITE_ATTRIBUTS + ") " + VALUES + "(" // ...
					+ "{0, number, ####} ," // id...
					+ " ''{1}'', " // name...
					+ "''{2}'', " // firmware ...
					+ "''{3}'', " // iptvkit...
					+ "''{4}'', " // comment ...
					+ " {5, number, ##########}," // date (ts)...
					+ " {6}," // log...
					+ " {7, number, ##########}, " // time...
					+ " {8, number, ####}, " // groupId...
					+ " {9, number, ####}, " // userId...
					+ " {10}" // readonly...
					+ ");"); // ...

	// =============================================
	// COUNT USERS, GROUP
	// =============================================

	// SELECT suite.userId, count(suite.userId) FROM `suite` group by
	// suite.userId
	public final static String COUNT_USERS = SELECT + DB_USER_ID + ", " + COUNT
			+ "(" + DB_USER_ID + ") " + FROM + TABLE_NAME + " " + GROUP_BY
			+ DB_USER_ID;

	public final static String COUNT_GROUPS = SELECT + DB_GROUP_ID + ", "
			+ COUNT + "(" + DB_GROUP_ID + ") " + FROM + TABLE_NAME + " "
			+ GROUP_BY + DB_GROUP_ID;
	// =============================
	// UPDATE
	// =============================

	public final static MessageFormat SQL_UPDATE_SUITE_INFO = new MessageFormat(
			UPDATE + TABLE_NAME + " " + SET + DB_IPTVKIT + " = ''{0}'', " // ...iptvkit
					+ DB_DATE + " = {1, number, ##########}, " // ...date (ts)
					+ DB_USER_ID + " = {2, number, ####}, " // ...userId
					+ DB_COMMENT + " = ''{3}'' " // ...comment
					+ WHERE + DB_ID + " = {4, number, ###}");

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(
			DELETE + FROM + TABLE_NAME + " " + WHERE_ONE_ENTRY);

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID
			+ ") " + FROM + TABLE_NAME;

	public final static String SQL_COUNT_SUITE = SELECT + COUNT_ALL + FROM
			+ TABLE_NAME + " ";

	public final static MessageFormat MF_COUNT_BY_NAME = new MessageFormat(
			SQL_COUNT_SUITE + WHERE + DB_NAME + " = ''{0}''");

	public final static MessageFormat MF_COUNT_BY_GROUP = new MessageFormat(
			SQL_COUNT_SUITE + WHERE + DB_GROUP_ID + " = {0, number, ####}");

	public final static MessageFormat MF_COUNT_BY_USER = new MessageFormat(
			SQL_COUNT_SUITE + WHERE + DB_USER_ID + " = {0, number, ####}");

}