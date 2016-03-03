package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoStatsCategoryInstance extends IDao {

	public static final String A = "a";
	public static final String TABLE_NAME = "catstatistic";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + A + " ";

	public static final String DB_SUITE_ID = "suiteId";
	public static final String DB_CATEGORY_ID = "categoryId";

	public static final String DB_RUNNING = "running";
	public static final String DB_RUNNING_SUCCESS = "success";
	public static final String DB_RUNNING_FAILURE = "failure";

	public static final String DB_RUNNING_ERROR = "error";
	public static final String DB_RUNNING_ERROR_CRASH = "errorCrash";
	public static final String DB_RUNNING_ERROR_TIMEOUT = "errorTimeout";
	public static final String DB_RUNNING_ERROR_EX = "errorEx";

	public static final String DB_SKIPPED = "skipped";
	public static final String DB_SKIPPED_DEP = "skippedDep";
	public static final String DB_SKIPPED_PRO = "skippedPro";

	// =============================================
	// SIMPLE STATS CAT
	// =============================================
	public final static String FROM_TABLE_NAME = FROM + TABLE_NAME + " ";
	public final static String STATS_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_SUITE_ID + ", " // ...
			+ DB_CATEGORY_ID + ", " // ...

			+ DB_RUNNING + ", " // ...
			+ DB_RUNNING_SUCCESS + ", " // ...
			+ DB_RUNNING_FAILURE + ", " // ...

			+ DB_RUNNING_ERROR + ", " // ...
			+ DB_RUNNING_ERROR_CRASH + ", " // ...
			+ DB_RUNNING_ERROR_TIMEOUT + ", " // ...
			+ DB_RUNNING_ERROR_EX + ", " // ...

			+ DB_SKIPPED + ", " // ...
			+ DB_SKIPPED_DEP + ", " // ...
			+ DB_SKIPPED_PRO + " "; // ...

	public final static String SQL_SELECT_STATS = SELECT + STATS_ATTRIBUTS + FROM_TABLE_NAME;

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_STATS + WHERE_ONE_ENTRY);

	public final static String SQL_DELETE_ALL = DELETE + FROM_TABLE_NAME + WHERE + "1";

	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM_TABLE_NAME
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_STATS = new MessageFormat(INSERT + TABLE_NAME + " (" + STATS_ATTRIBUTS
			+ ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " {1, number, ####}, " // suiteId ...
			+ " {2, number, ####}, " // categoryId...

			+ " {3, number, ####}, " // running...
			+ " {4, number, ####}, " // runningSuccess...
			+ " {5, number, ####}, " // runningFailure...

			+ " {6, number, ####}, " // runningError...
			+ " {7, number, ####}, " // runningErrorCrash...
			+ " {8, number, ####}, " // runningErrorTimeout...
			+ " {9, number, ####}, " // runningErrorEx...

			+ " {10, number, ####}, " // skipped...
			+ " {11, number, ####}, " // skippedDep...
			+ " {12, number, ####} " // skippedPro...

			+ ");"); // ...

	// ===================================
	// DIVERS
	// ===================================

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM_TABLE_NAME;

	public final static String SQL_COUNT_STATS = SELECT + COUNT_ALL + FROM_TABLE_NAME;

	// ===================================
	// FOR SUITE
	// ===================================

	public final static String WHERE_SUITE = WHERE + DB_SUITE_ID + EGAL_NUMBER;

	public final static MessageFormat MF_COUNT_STATS_FOR_SUITE = new MessageFormat(SQL_COUNT_STATS + WHERE_SUITE);

	public final static MessageFormat MF_SELECT_STATS_FOR_SUITE = new MessageFormat(SQL_SELECT_STATS + WHERE_SUITE);

	public final static MessageFormat MF_DELETE_FOR_SUITE = new MessageFormat(DELETE + FROM_TABLE_NAME + WHERE_SUITE);

}
