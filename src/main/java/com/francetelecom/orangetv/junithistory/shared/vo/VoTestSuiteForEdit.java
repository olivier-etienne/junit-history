package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.Date;

public class VoTestSuiteForEdit extends AbstractVoIdName {

	private static final long serialVersionUID = 1L;

	private String firmware;
	private String iptvkit;
	private Date date;
	private int userId = IVo.ID_UNDEFINED;

	private String comment;

	private boolean readOnly = false;

	// ------------------------------- constructor
	public VoTestSuiteForEdit() {
	}

	public VoTestSuiteForEdit(int id, String name) {
		super(id, name);
	}

	// -------------------------------- accessors

	public String getFirmware() {
		return firmware;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
