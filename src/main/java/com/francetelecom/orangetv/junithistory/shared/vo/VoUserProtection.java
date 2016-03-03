package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoUserProtection extends VoItemProtection {

	private static final long serialVersionUID = 1L;

	private boolean canUpdateName = false;

	public boolean canUpdateName() {
		return canUpdateName;
	}

	public void setCanUpdateName(boolean canUpdateName) {
		this.canUpdateName = canUpdateName;
	}

}
