package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoGroupProtection extends VoItemProtection {
	private static final long serialVersionUID = 1L;

	private boolean canUpdatePrefix = false;

	public boolean canUpdatePrefix() {
		return canUpdatePrefix;
	}

	public void setCanUpdatePrefix(boolean canUpdatePrefix) {
		this.canUpdatePrefix = canUpdatePrefix;
	}

}
