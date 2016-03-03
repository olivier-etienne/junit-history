package com.francetelecom.orangetv.junithistory.server.model;

import java.util.List;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;

/**
 * Instance concrete d'un test
 * 
 * @author ndmz2720
 *
 */
public class DbTestInstance extends AbstractDbEntry {

	private static final long serialVersionUID = 1L;

	private DbTestSuiteInstance testSuiteInstance; // lazy & required
	private String name; // required
	private TestSubStatusEnum status; // required
	private DbTestClass tClass; // required

	// time in ms
	private long time;

	// 0..1 message
	private DbTestMessage message; // lazy
	// 0..n comment
	private List<DbTestComment> listComments; // lazy

	// ------------------------------------ constructor

	public DbTestInstance(DbTestSuiteInstance testSuiteInstance, DbTestClass tClass) {
		this.testSuiteInstance = testSuiteInstance;
		this.tClass = tClass;
	}

	// --------------------------------- accessors
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TestSubStatusEnum getStatus() {
		return status;
	}

	public void setStatus(TestSubStatusEnum status) {
		this.status = status;
	}

	public DbTestClass gettClass() {
		return tClass;
	}

	public void settClass(DbTestClass tClass) {
		this.tClass = tClass;
	}

	public DbTestMessage getMessage() {
		return message;
	}

	public void setMessage(DbTestMessage message) {
		this.message = message;
	}

	public List<DbTestComment> getListComments() {
		return listComments;
	}

	public void setListComments(List<DbTestComment> listComments) {
		this.listComments = listComments;
	}

	public DbTestSuiteInstance getTestSuiteInstance() {
		return testSuiteInstance;
	}

	public String getName() {
		return name;
	}

	// ------------------------------ overring Object
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append(" - suite: ");
		sb.append(this.testSuiteInstance.getName());
		sb.append(" - time: ");
		sb.append(this.time);
		sb.append(" - status: ");
		sb.append(this.status.name());

		if (this.tClass != null) {
			sb.append("[class: ");
			sb.append(this.tClass.toString());
			sb.append("]");
		}

		return sb.toString();
	}

}
