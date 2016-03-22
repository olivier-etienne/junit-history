package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Encapule un TestInstance pour la visualisation et l'edition
 * 
 * @SuppressWarnings("serial")
 * @author NDMZ2720
 *
 */
public class VoTestInstanceForEdit extends VoIdName {

	private static final long serialVersionUID = 1L;

	private String status;
	private boolean success;
	private boolean skipped;

	private String suiteName;
	private String suiteDate;

	// message
	private String type;
	private String message;
	private String stackTrace;
	private String outputLog;

	// comment
	private String comment;

	// ---------------------------- constructor
	public VoTestInstanceForEdit() {
		super();
	}

	public VoTestInstanceForEdit(int id, String name) {
		super(id, name);
	}

	// -------------------------------- accessors

	public String getType() {
		return type;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getSuiteDate() {
		return suiteDate;
	}

	public void setSuiteDate(String suiteDate) {
		this.suiteDate = suiteDate;
	}

	public boolean isSkipped() {
		return skipped;
	}

	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public boolean hasComment() {
		return this.comment != null;
	}

}
