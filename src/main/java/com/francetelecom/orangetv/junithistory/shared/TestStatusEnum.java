package com.francetelecom.orangetv.junithistory.shared;

public enum TestStatusEnum {
	Success, Failure, Error, Skipped;

	public String getStyleName() {
		return "Status " + this.name();
	}
}