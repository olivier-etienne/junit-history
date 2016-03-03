package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoSingleReportProtection implements IVo {

	private static final long serialVersionUID = 1L;

	private boolean addToHistory = false;

	public void setCanAddToHistory(boolean canAddToHistory) {
		this.addToHistory = canAddToHistory;
	}

	public boolean canAddToHistory() {
		return this.addToHistory;
	}

}
