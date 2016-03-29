package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoGroupForGrid extends VoIdName {

	private static final long serialVersionUID = 1L;

	private String stb;
	private String prefix;

	// -------------------------------- constructor
	public VoGroupForGrid() {
	}

	public VoGroupForGrid(int id, String name) {
		super(id, name);
	}

	// ------------------------------------- accessors
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

}
