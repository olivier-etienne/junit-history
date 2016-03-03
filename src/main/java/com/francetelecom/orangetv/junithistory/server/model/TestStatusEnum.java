package com.francetelecom.orangetv.junithistory.server.model;

public enum TestStatusEnum {
	Success, Failure, Error, Skipped;

	public String getStyleName() {
		return "Status " + this.name();
	}
}