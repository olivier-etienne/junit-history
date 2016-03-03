package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.Date;

public class VoSingleReportData implements IVo {

	private static final long serialVersionUID = 1L;

	private Date date;
	private int groupId = ID_UNDEFINED;
	private String firmware;
	private String iptvkit;
	private int userId = ID_UNDEFINED;
	private String comment;

	// ------------------------------ constructor
	public VoSingleReportData() {
	}

	// ------------------------------------- accessors
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
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
