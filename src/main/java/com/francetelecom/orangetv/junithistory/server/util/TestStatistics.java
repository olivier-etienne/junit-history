package com.francetelecom.orangetv.junithistory.server.util;

import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatistics.AbstractCompteur;

public class TestStatistics extends AbstractCompteur {

	private int running = 0;
	private int running_success = 0;
	private int running_failure = 0;

	private int running_error = 0;
	private int running_error_crash = 0;
	private int running_error_timeout = 0;
	private int running_error_exception = 0;

	private int skipped = 0;
	private int skipped_dependency = 0;
	private int skipped_programmaticaly = 0;

	// evolution (facultatif)
	private boolean up = false;

	// --------------------------------------- accessors

	public int getRunning() {
		return running;
	}

	public boolean isUp() {
		return up;
	}

	public void setUp(boolean up) {
		this.up = up;
	}

	public void setRunning(int running) {
		this.running = running;
	}

	@Override
	public int getRunningSuccess() {
		return running_success;
	}

	public void setRunningSuccess(int running_success) {
		this.running_success = running_success;
	}

	@Override
	public int getRunningFailure() {
		return running_failure;
	}

	public void setRunningFailure(int running_failure) {
		this.running_failure = running_failure;
	}

	@Override
	public int getRunningError() {
		return running_error;
	}

	public void setRunningError(int running_error) {
		this.running_error = running_error;
	}

	@Override
	public int getRunningErrorCrash() {
		return running_error_crash;
	}

	public void setRunningErrorCrash(int running_error_crash) {
		this.running_error_crash = running_error_crash;
	}

	@Override
	public int getRunningErrorTimeout() {
		return running_error_timeout;
	}

	public void setRunningErrorTimeout(int running_error_timeout) {
		this.running_error_timeout = running_error_timeout;
	}

	@Override
	public int getRunningErrorException() {
		return running_error_exception;
	}

	public void setRunningErrorException(int running_error_exception) {
		this.running_error_exception = running_error_exception;
	}

	public int getSkipped() {
		return skipped;
	}

	public void setSkipped(int skipped) {
		this.skipped = skipped;
	}

	@Override
	public int getSkippedDependency() {
		return skipped_dependency;
	}

	public void setSkippedDependency(int skipped_dependency) {
		this.skipped_dependency = skipped_dependency;
	}

	@Override
	public int getSkippedProgrammaticaly() {
		return skipped_programmaticaly;
	}

	public void setSkippedProgrammaticaly(int skipped_programmaticaly) {
		this.skipped_programmaticaly = skipped_programmaticaly;
	}

	// -------------------------------------- public methods
	public void addTestStatistics(TestStatistics testStatistics) {
		if (testStatistics == null) {
			return;
		}
		this.running += testStatistics.getRunning();
		this.running_error += testStatistics.getRunningError();
		this.running_error_crash += testStatistics.getRunningErrorCrash();
		this.running_error_exception += testStatistics.getRunningErrorException();
		this.running_error_timeout += testStatistics.getRunningErrorTimeout();
		this.running_failure += testStatistics.getRunningFailure();
		this.running_success += testStatistics.getRunningSuccess();
		this.skipped += testStatistics.getSkipped();
		this.skipped_dependency += testStatistics.getSkippedDependency();
		this.skipped_programmaticaly += testStatistics.getSkippedProgrammaticaly();
	}

	// ----------------------------- overriding Object
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("success: ");
		sb.append(this.running_success);
		sb.append(" - failures: ");
		sb.append(this.running_failure);
		sb.append(" - errors: ");
		sb.append(this.running_error);
		return sb.toString();
	}

}
