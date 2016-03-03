package com.francetelecom.orangetv.junithistory.shared.util;

public class JUnitHistoryException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public JUnitHistoryException() {
	}

	public JUnitHistoryException(String message) {
		super(message);
		this.errorMessage = message;
	}

	public JUnitHistoryException(Exception exception) {

		super(exception.getMessage());
		this.buildErrorMessage(exception);
	}

	private void buildErrorMessage(Exception exception) {

		this.errorMessage = exception.getMessage();

		if (exception.getCause() != null) {
			this.errorMessage += " (" + exception.getCause().getMessage() + ")";
		}
	}

}
