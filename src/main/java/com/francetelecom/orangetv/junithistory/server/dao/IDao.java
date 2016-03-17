package com.francetelecom.orangetv.junithistory.server.dao;

public interface IDao {

	public static final String P = ".";
	public static final String DB_ID = "id";

	public static final String AS = " as ";
	public static final String SELECT = "select ";
	public static final String MAX = "max";
	public static final String AND = "and ";
	public static final String OR = "or ";
	public static final String LIKE = "like ";
	public static final String COUNT_ALL = "count(*) ";
	public static final String COUNT = "count";
	public static final String UPDATE = "update ";
	public static final String INSERT = "insert into ";
	public static final String VALUES = "values ";
	public static final String SET = "set ";
	public static final String FROM = "from ";
	public static final String WHERE = "where ";
	public static final String DISTINCT = "distinct ";
	public static final String ORDER_BY = "order by ";
	public static final String DELETE = "delete ";
	public static final String GROUP_BY = "group by ";
	public static final String LEFT_JOIN = "left join ";
	public static final String ON = "on ";
	public static final String SUM = "sum";
	public static final String DESC = "desc ";

	public final static String EGAL_NUMBER = " = {0, number, ####} ";
	public final static String WHERE_ONE_ENTRY = WHERE + DB_ID + EGAL_NUMBER;
	public final static String AND_ONE_ENTRY = AND + DB_ID + EGAL_NUMBER;
}
