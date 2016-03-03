package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

public class VoSingleReportValidation implements IVo {

	private static final long serialVersionUID = 1L;

	private List<String> errorMessages;

	// ---------------------------- accessor
	public boolean isValid() {
		return this.errorMessages == null || this.errorMessages.isEmpty();
	}

	public List<String> getErrorMessages() {
		if (this.errorMessages == null) {
			this.errorMessages = new ArrayList<>();
		}
		return this.errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

}
