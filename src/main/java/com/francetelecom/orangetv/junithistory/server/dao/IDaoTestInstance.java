package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestInstance extends IDao {

	public static final String T = "t";
	public static final String TP = T + P;
	public static final String TABLE_NAME = "test";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + T + " ";

	public static final String DB_NAME = "name";
	public static final String DB_STATUS = "status";
	public static final String DB_TIME = "time";

	public static final String DB_SUITE_ID = "suiteId";
	public static final String DB_TCLASS_ID = "tclassId";

	// =============================================
	// SIMPLE TEST INSTANCE
	// =============================================
	public final static String FROM_TABLE_NAME = FROM + TABLE_NAME + " ";
	public final static String TEST_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_NAME + ", " // ...
			+ DB_STATUS + ", " // ...
			+ DB_SUITE_ID + ", " // ...
			+ DB_TIME + ", " // ...
			+ DB_TCLASS_ID + " "; // ...

	public final static String SQL_SELECT_TEST = SELECT + TEST_ATTRIBUTS + FROM_TABLE_NAME;

	// =============================================
	// TEST MESSAGE ATTRIBUT JOIN...
	// ============================================
	public final static String TEST_ATTRIBUTS_JOIN = // ...
	TP + DB_ID + ", " // ...
			+ TP + DB_NAME + ", " // ...
			+ TP + DB_STATUS + ", " // ...
			+ TP + DB_SUITE_ID + ", " // ...
			+ TP + DB_TIME + ", " // ...
			+ TP + DB_TCLASS_ID + " ";// ...

	// =============================================
	// TEST INSTANCE WITH MESSAGEID
	// =============================================
	public final static String MESS_ID = IDaoTestMessage.MP + IDaoTestMessage.DB_ID;
	public final static String MESS_TEST_ID = IDaoTestMessage.MP + IDaoTestMessage.DB_TEST_ID;
	public final static String TEST_ATTRIBUTS_JOIN_MESS = // ...
	TEST_ATTRIBUTS_JOIN + ", "// ...
			+ MESS_ID + " "; // ...

	// left join message as m on t.id = m.testId
	public final static String LEFT_JOIN_MESS = LEFT_JOIN + IDaoTestMessage.TABLE_ALIAS + ON + TP + DB_ID + " = "
			+ MESS_TEST_ID + " ";

	public final static String SQL_SELECT_TEST_JOIN_MESS = SELECT + TEST_ATTRIBUTS_JOIN_MESS + FROM + TABLE_ALIAS
			+ LEFT_JOIN_MESS;

	public final static MessageFormat MF_SELECT_ONE_ENTRY_JOIN_MESS = new MessageFormat(SQL_SELECT_TEST_JOIN_MESS
			+ WHERE + TP + DB_ID + EGAL_NUMBER);

	// ===================================
	// DIVERS
	// ===================================

	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM_TABLE_NAME;

	public final static String SQL_COUNT_TEST = SELECT + COUNT_ALL + FROM_TABLE_NAME;

	// ===============================================
	// TEST INSTANCE FOR GROUP_ID AND NAME
	// =============================================
	public final static String SUITE_ID = IDaoTestSuiteInstance.SP + IDaoTestSuiteInstance.DB_ID;
	public final static String SUITE_GROUPID = IDaoTestSuiteInstance.SP + IDaoTestSuiteInstance.DB_GROUP_ID;

	// left join suite on test.suiteId = suite.id
	public final static String LEFT_JOIN_SUITE = LEFT_JOIN + IDaoTestSuiteInstance.TABLE_ALIAS + ON + TP + DB_SUITE_ID
			+ " = " + SUITE_ID + " ";

	public final static String SQL_SELECT_JOIN_SUITE = SELECT + TEST_ATTRIBUTS_JOIN_MESS + FROM + TABLE_ALIAS
			+ LEFT_JOIN_SUITE;

	// WHERE suite.groupId = 11 and test.name like '%Schedule%'
	public final static String WHERE_WITH_GROUP_AND_TEST_NAME = WHERE // ...
			+ SUITE_GROUPID + EGAL_NUMBER // ...
			+ AND // ...
			+ TP + DB_NAME + " " + LIKE + " ''%{1}%''";

	public final static MessageFormat MF_SELECT_WITH_GROUP_AND_TEST_NAME = new MessageFormat(SQL_SELECT_JOIN_SUITE
			+ LEFT_JOIN_MESS + WHERE_WITH_GROUP_AND_TEST_NAME);

	public final static MessageFormat MF_COUNT_WITH_GROUP_AND_TEST_NAME = new MessageFormat(SELECT + COUNT_ALL + FROM
			+ TABLE_ALIAS + LEFT_JOIN_SUITE // ...
			+ WHERE_WITH_GROUP_AND_TEST_NAME // ...

	);

	// =============================================
	// CREATE, DELETE
	// =============================================
	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM_TABLE_NAME
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_TEST = new MessageFormat(INSERT + TABLE_NAME + " (" + TEST_ATTRIBUTS
			+ ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " ''{1}'', " // name...
			+ "''{2}'', " // status ...
			+ " {3, number, ####}," // suiteId...
			+ " {4, number, ##########}, " // time...
			+ " {5, number, #####}" // tclassId...
			+ ");"); // ...

	// ===================================
	// FOR SUITE WITH MESS ID
	// ===================================

	public final static String WHERE_SUITE = WHERE + DB_SUITE_ID + EGAL_NUMBER;

	public final static MessageFormat MF_SELECT_TEST_JOIN_MESS_FOR_SUITE = new MessageFormat(SQL_SELECT_TEST_JOIN_MESS
			+ WHERE_SUITE);

	public final static MessageFormat MF_COUNT_TEST_FOR_SUITE = new MessageFormat(SQL_COUNT_TEST + WHERE_SUITE);

	public final static MessageFormat MF_DELETE_FOR_SUITE = new MessageFormat(DELETE + FROM_TABLE_NAME + WHERE_SUITE);

}
