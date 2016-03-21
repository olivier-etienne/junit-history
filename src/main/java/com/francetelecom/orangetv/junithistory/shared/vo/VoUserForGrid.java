package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoUserForGrid extends VoIdName {

	private static final long serialVersionUID = 1L;

	private String description;
	private boolean admin = false;

	// ------------------------------- constructor
	public VoUserForGrid() {
	}

	public VoUserForGrid(int id, String name) {
		super(id, name);
	}

	// ---------------------------- accessors

	public String getDescription() {
		return description == null ? "" : this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
