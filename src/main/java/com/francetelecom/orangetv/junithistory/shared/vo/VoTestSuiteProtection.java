package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoTestSuiteProtection implements IVo {

	private static final long serialVersionUID = 1L;

	private boolean delete = false;
	private boolean edit = false;

	// --------------------- accessors

	public boolean canEdit() {
		return this.edit;
	}

	public void setCanEdit(boolean canEdit) {
		this.edit = canEdit;
	}

	public boolean canDelete() {
		return this.delete;
	}

	public void setCanDelete(boolean canDelete) {
		this.delete = canDelete;
	}

}
