package com.francetelecom.orangetv.junithistory.server.dao;

import java.text.MessageFormat;

public interface IDaoTestMessage extends IDao {

	public static final String M = "m";
	public static final String MP = M + P;

	public static final String TABLE_NAME = "message";
	public static final String TABLE_ALIAS = TABLE_NAME + AS + M + " ";

	public static final String DB_TYPE = "type";
	public static final String DB_MESSAGE = "message";
	public static final String DB_STACK_TRACE = "stacktrace";
	public static final String DB_OUPUT_LOG = "outputlog";

	public static final String DB_TEST_ID = "testId";

	// =============================================
	// SIMPLE TEST MESSAGE
	// =============================================
	public final static String MESSAGE_ATTRIBUTS = DB_ID + ", " // ...
			+ DB_TYPE + ", " // ...
			+ DB_MESSAGE + ", " // ...
			+ DB_STACK_TRACE + ", " // ...
			+ DB_OUPUT_LOG + ", " // ...
			+ DB_TEST_ID + " "; // ...

	public final static String SQL_SELECT_MESSAGE = SELECT + MESSAGE_ATTRIBUTS + FROM + TABLE_NAME + " ";

	public final static MessageFormat MF_SELECT_ONE_ENTRY = new MessageFormat(SQL_SELECT_MESSAGE + WHERE_ONE_ENTRY);

	public final static MessageFormat MF_SELECT_FOR_TEST = new MessageFormat(SQL_SELECT_MESSAGE + WHERE + DB_TEST_ID
			+ EGAL_NUMBER);

	// =============================================
	// TEST MESSAGE WITH JOIN
	// =============================================
	public final static String MESSAGE_ATTRIBUTS_FOR_JOIN = MP + DB_ID + ", " // ...
			+ MP + DB_TYPE + ", " // ...
			+ MP + DB_MESSAGE + ", " // ...
			+ MP + DB_STACK_TRACE + ", " // ...
			+ MP + DB_OUPUT_LOG + ", " // ...
			+ MP + DB_TEST_ID + " "; // ...

	public final static String FROM_JOIN_TEST = FROM + TABLE_ALIAS + ", " + IDaoTestInstance.TABLE_ALIAS + WHERE + MP
			+ DB_TEST_ID + " = " + IDaoTestInstance.TP + IDaoTestInstance.DB_ID + " ";
	// SELECT m.id, m.message, m.testId FROM message as m, test as t WHERE
	// m.testId = t.id
	public final static String SQL_SELECT_MESSAGE_JOINT_TEST = SELECT + MESSAGE_ATTRIBUTS_FOR_JOIN + FROM_JOIN_TEST;

	// tous les messages d'une suite
	public final static MessageFormat MF_SELECT_JOIN_TEST_FOR_SUITE = new MessageFormat(SQL_SELECT_MESSAGE_JOINT_TEST
			+ AND + IDaoTestInstance.TP + IDaoTestInstance.DB_SUITE_ID + EGAL_NUMBER);

	// =============================================
	// DIVERS
	// =============================================
	public final static MessageFormat MF_DELETE_ONE_ENTRY = new MessageFormat(DELETE + FROM + TABLE_NAME + " "
			+ WHERE_ONE_ENTRY);

	public final static MessageFormat MF_CREATE_MESSAGE = new MessageFormat(INSERT + TABLE_NAME + " ("
			+ MESSAGE_ATTRIBUTS + ") " + VALUES + "(" // ...
			+ "{0, number, ####} ," // id...
			+ " ''{1}'', " // type...
			+ "''{2}'', " // message ...
			+ "''{3}'', " // stacktrace ...
			+ "''{4}'', " // outputlogs...
			+ "{5, number, ####} " // testId...
			+ ");"); // ...

	public final static String SQL_COUNT_TEST = COUNT_ALL + FROM + TABLE_NAME;
	public final static String SQL_SELECT_MAX_ID = SELECT + MAX + "(" + DB_ID + ") " + FROM + TABLE_NAME;

}
