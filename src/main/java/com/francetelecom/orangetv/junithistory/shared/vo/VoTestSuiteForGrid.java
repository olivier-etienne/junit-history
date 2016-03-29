package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoTestSuiteForGrid extends VoIdName {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;

	private String firmware;
	private String iptvkit;

	private String date;
	private String user;
	private long time;

	private boolean readonly;

	private String urlToShare;

	// ------------------------------ constructor
	public VoTestSuiteForGrid() {
	}

	public VoTestSuiteForGrid(int id, String name) {
		super(id, name);
	}

	// --------------------------------- accessor

	public String getFirmware() {
		return firmware;
	}

	public String getUrlToShare() {
		return urlToShare;
	}

	public void setUrlToShare(String urlToShare) {
		this.urlToShare = urlToShare;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
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

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

}
