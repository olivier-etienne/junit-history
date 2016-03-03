package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoGroupForEdit extends AbstractVoIdName {

	private static final long serialVersionUID = 1L;

	private String stb;
	private String prefix;

	// ------------------------------- constructor
	public VoGroupForEdit() {
	}

	public VoGroupForEdit(int id, String name) {
		super(id, name);
	}

	// ---------------------------- accessors
	public void setName(String name) {
		super.setName(name);
	}

	public String getStb() {
		return stb;
	}

	public void setStb(String stb) {
		this.stb = stb;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public VoGroupProtection getGroupProtection() {

		return (VoGroupProtection) super.getProtection();
	}

}
