package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoSingleReportResponse implements IVo {

	private static final long serialVersionUID = 1L;

	private String url;
	private int groupId;
	private String suiteName;
	private String version;

	// ---------------------------------- accessors

	public String getUrl() {
		return url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

}
