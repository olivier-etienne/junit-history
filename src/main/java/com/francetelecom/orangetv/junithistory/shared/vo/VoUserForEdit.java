package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Edition d'un user non admin
 * 
 * @author sylvie
 * 
 */
public class VoUserForEdit extends AbstractVoIdName {

	private static final long serialVersionUID = 1L;

	private String description;

	// ------------------------------- constructor
	public VoUserForEdit() {
	}

	public VoUserForEdit(int id, String name) {
		super(id, name);
	}

	// ---------------------------- accessors
	public void setName(String name) {
		super.setName(name);
	}

	public String getDescription() {
		return description == null ? "" : this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public VoUserProtection getUserProtection() {
		return this.getProtection() == null ? null : (VoUserProtection) this.getProtection();
	}

}
