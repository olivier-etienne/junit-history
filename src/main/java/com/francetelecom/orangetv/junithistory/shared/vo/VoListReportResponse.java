package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoListReportResponse implements IVo {

	private static final long serialVersionUID = 1L;

	private String url;
	private String comment;

	public VoListReportResponse() {
		this(null, null);
	}

	public VoListReportResponse(String url, String comment) {
		this.url = url;
		this.comment = comment;
	}

	// ------------------------ accessors
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
