package com.francetelecom.orangetv.junithistory.server.model;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;

/**
 * Encapsule la description des erreurs du test
 * 
 * @author ndmz2720
 *
 */
public class DbTestMessage extends AbstractDbEntry {

	private static final long serialVersionUID = 1L;

	private String type;
	private String message;
	private String stackTrace;
	private String outputLog;

	// ----------------------------- constructor
	public DbTestMessage(String type) {
		this.type = type;
	}

	protected DbTestMessage() {
	}

	// ----------------------------------- accessor

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStackTrace() {
		return stackTrace;
	}

	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}

	public String getOutputLog() {
		return outputLog;
	}

	public void setOutputLog(String outputLog) {
		this.outputLog = outputLog;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	// ------------------------------ overring Object
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(this.type);
		sb.append(" - message: ");
		sb.append(this.message);

		return sb.toString();
	}

}
