package com.francetelecom.orangetv.junithistory.server.model;

public enum TestSubStatusEnum {
	success(TestStatusEnum.Success, "Success"), dependency(TestStatusEnum.Skipped, "Failure (dep)"), notImplemented(
			TestStatusEnum.Failure, " Not implemented"), skipped(TestStatusEnum.Skipped, "SKIPPED"), failure(
			TestStatusEnum.Failure, "Failure"), crash(TestStatusEnum.Error, "Crash"), timeout(TestStatusEnum.Error,
			"Timeout"), error(TestStatusEnum.Error, "Error");

	final TestStatusEnum status;

	public TestStatusEnum getStatus() {
		return this.status;
	}

	private final String textLabel;

	public String getLabel() {
		return this.textLabel;
	}

	TestSubStatusEnum(TestStatusEnum status, String textLabel) {
		this.status = status;
		this.textLabel = textLabel;
	}
}