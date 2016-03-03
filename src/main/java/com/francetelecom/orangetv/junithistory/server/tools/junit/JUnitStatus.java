package com.francetelecom.orangetv.junithistory.server.tools.junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.francetelecom.orangetv.junithistory.server.model.TestStatusEnum;
import com.francetelecom.orangetv.junithistory.server.model.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitFailureOrError;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitTestCase;
import com.francetelecom.orangetv.junithistory.server.util.HtmlUtils;

public class JUnitStatus {

	private static final Pattern SKIPPED_PATTERN = Pattern.compile(TestState.SKIPPED.name());
	private static final Pattern NOT_IMPLEMENTED_PATTERN = Pattern
			.compile("Test ([\\w\\.]+)\\(([\\w\\.]+)\\) not implemented");
	private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("Dependency (\\w+)\\(([\\w\\.]+)\\) not passed");
	private static final String SYSTEM_CRASH_ERROR_CLASS = "com.francetelecom.orangetv.gwt.junit.client.SystemCrashError";
	private static final String TIMEOUT_ERROR_CLASS = "com.google.gwt.junit.client.TimeoutException";
	private static final String TIMEOUT_FAILURE_CLASS = "com.google.gwt.junit.client.TimeoutFailure";

	/**
	 * Represents a test result
	 */
	static enum TestState {
		/**
		 * The test has not run (yet)
		 */
		NOT_RUN,
		/**
		 * The test was skipped
		 */
		SKIPPED,
		/**
		 * The test is running, the system is expecting its completion
		 */
		RUNNING,
		/**
		 * The test has been successfully passed
		 */
		SUCCESS,
		/**
		 * The test has failed (functional)
		 */
		FAILURE,
		/**
		 * The test has failed due to a technical error (exception)
		 */
		ERROR,
		/**
		 * The test has been skipped because it is suspected to have caused a
		 * system crash
		 */
		CRASH
	}

	public static class JUnitTestCaseStatus {

		JUnitTestCase tc;
		TestSubStatusEnum subStatus;
		// String label;
		String detail;

		JUnitTestCaseStatus(JUnitTestCase tc, TestSubStatusEnum subStatus, String detail) {
			this.tc = tc;
			this.subStatus = subStatus;
			this.detail = detail;
		}

		String getStyleName() {
			return this.getStatus().getStyleName();
		}

		boolean isSuccess() {
			return this.getStatus() == TestStatusEnum.Success;
		}

		boolean isFailure() {
			return this.getStatus() == TestStatusEnum.Failure;
		}

		boolean isError() {
			return this.getStatus() == TestStatusEnum.Error;
		}

		public boolean isSkipped() {
			return this.getStatus() == TestStatusEnum.Skipped;
		}

		public TestStatusEnum getStatus() {
			return this.subStatus.getStatus();
		}

		public TestSubStatusEnum getSubStatus() {
			return this.subStatus;
		}
	}

	static String getMessage(JUnitFailureOrError foe) {
		/*
		 * trick:
		 * message: "[Remote test failed at bla bla bla] Dependency testPause(com.francetelecom.orangetv.gwt.stb.test.GwtTestTimeShift) not passed: ERROR"
		 * stack: "junit.framework.AssertionFailedError: Remote test failed at bla bla bla\n
		 * 		  "rest of stack"
		 * not easy to find the end of the "Remote test failed ..." message unless
		 */
		String message = foe.getMessage();
		if (message.startsWith("Remote test failed at")) {
			String stack = foe.getStack();
			int idxStart = stack.indexOf(':');
			if (idxStart > 0) {
				idxStart += 2;
				int idxEnd = stack.indexOf('\n', idxStart);
				if (idxEnd > 0) {
					String sub = stack.substring(idxStart, idxEnd);
					if (sub.startsWith("Remote test failed at")) {
						// we got it: strip string from message
						return message.length() <= sub.length() ? null : message.substring(sub.length());
					}
				}
			}
		}
		return message;
	}

	public static JUnitTestCaseStatus findTestStatus(JUnitTestCase tc) {

		if (tc == null) {
			return null;
		}

		JUnitFailureOrError foe = null;
		TestSubStatusEnum subStatus = TestSubStatusEnum.success;
		if (tc.getFailure() != null) {
			foe = tc.getFailure();
			subStatus = TestSubStatusEnum.failure;
		} else if (tc.getError() != null) {
			foe = tc.getError();
			subStatus = TestSubStatusEnum.error;
		}
		if (foe == null) {
			return new JUnitTestCaseStatus(tc, TestSubStatusEnum.success, "");
		}

		// Failure or Error
		// >> find label and detail...
		String details = null;
		if (foe.getStack().contains(SYSTEM_CRASH_ERROR_CLASS)) {
			// SystemCrashError: CRASH
			details = "System Crash";
			subStatus = TestSubStatusEnum.crash;
		} else if (foe.getStack().contains(TIMEOUT_ERROR_CLASS)) {
			int idx1 = foe.getStack().indexOf(TIMEOUT_ERROR_CLASS);
			int idx2 = foe.getStack().indexOf('\n', idx1 + TIMEOUT_ERROR_CLASS.length());
			details = foe.getStack().substring(idx1 + TIMEOUT_ERROR_CLASS.length() + 2, idx2);
			subStatus = TestSubStatusEnum.timeout;
		} else if (foe.getStack().contains(TIMEOUT_FAILURE_CLASS)) {
			int idx1 = foe.getStack().indexOf(TIMEOUT_FAILURE_CLASS);
			int idx2 = foe.getStack().indexOf('\n', idx1 + TIMEOUT_FAILURE_CLASS.length());
			details = foe.getStack().substring(idx1 + TIMEOUT_FAILURE_CLASS.length() + 2, idx2);
			subStatus = TestSubStatusEnum.timeout;
		} else {
			// remove first message line if "Remote test failed..."
			String message = getMessage(foe);
			if (message == null) {
				details = "Type: " + foe.getType();
			} else {
				// dependency failure contains:
				// Caused by:
				// com.francetelecom.orangetv.gwt.junit.client.DependencyFailedError:
				// Dependency
				// testChangePosition(com.francetelecom.orangetv.gwt.stb.test.GwtTestTimeShift)
				// not passed: ERROR
				Matcher m = DEPENDENCY_PATTERN.matcher(message);
				if (m.find()) {
					// DependencyFailedError: FAILURE due to dependency
					subStatus = TestSubStatusEnum.dependency;
					details = "Dependency Failed: " + m.group(2) + "." + m.group(1);
					// if (message.indexOf("SKIPPED") > -1) {
					// }
				} else {

					m = NOT_IMPLEMENTED_PATTERN.matcher(message);
					if (m.find()) {
						// NotImplementedFailedError: FAILURE due to no
						// implemented method
						subStatus = TestSubStatusEnum.notImplemented;
						details = "Test " + m.group(2) + "." + m.group(1) + " is not implemented!!";
					} else {

						m = SKIPPED_PATTERN.matcher(message);
						if (m.find()) {
							// NotImplementedFailedError: FAILURE due to no
							// implemented method
							subStatus = TestSubStatusEnum.skipped;
							details = "Test has been skipped!!";
						} else {
							// default case
							details = "Type: " + foe.getType() + "\nMessage: " + HtmlUtils.encode2Title(message);
						}
					}
				}
			}
		}
		return new JUnitTestCaseStatus(tc, subStatus, details);
	}

}
