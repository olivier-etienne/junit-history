package com.francetelecom.orangetv.junithistory.server.model;

import java.util.Date;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;

/**
 * Passage d'un ensemble de tests sur un firmware à une date donnée
 * 
 * @author ndmz2720
 *
 */
public class DbTestSuiteInstance extends AbstractDbEntry {
	private static final long serialVersionUID = 1L;

	private DbTestSuiteGroup testSuiteGroup; // required
	private Date date;
	private DbTestUser user;
	private long time;

	private String firmware;
	private String iptvkit;

	// porte le prefixe des noms de fichiers
	private String name;

	private String comment;
	private boolean logExists;

	private boolean readonly;

	// ---------------------------------- constructor
	public DbTestSuiteInstance() {

	}

	public DbTestSuiteInstance(DbTestSuiteGroup testSuiteGroup) {
		this.testSuiteGroup = testSuiteGroup;
	}

	// ------------------------------------- accessors

	public boolean isLogExists() {
		return this.logExists;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public void setTestSuiteGroup(DbTestSuiteGroup testSuiteGroup) {
		this.testSuiteGroup = testSuiteGroup;
	}

	public void setLogExists(boolean logExists) {
		this.logExists = logExists;
	}

	public Date getDate() {
		return date;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public DbTestUser getUser() {
		return user;
	}

	public void setUser(DbTestUser user) {
		this.user = user;
	}

	public String getFirmware() {
		return firmware;
	}

	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	public String getIptvkit() {
		return iptvkit;
	}

	public void setIptvkit(String iptvkit) {
		this.iptvkit = iptvkit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public DbTestSuiteGroup getTestSuiteGroup() {
		return testSuiteGroup;
	}

	// ------------------------------ overring Object
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.name);
		sb.append(" - group: ");
		sb.append((this.testSuiteGroup == null) ? "null" : this.testSuiteGroup.getName());

		return sb.toString();
	}
}
