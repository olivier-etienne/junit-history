package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoCategoryForGrid extends VoIdName {

	private static final long serialVersionUID = 1L;

	public static final char SEPARATOR = ',';

	private String[] suiteNames;
	private boolean defaultValue = false;

	// -------------------------- constructor
	public VoCategoryForGrid() {
	}

	public VoCategoryForGrid(int id, String name) {
		super(id, name);
	}

	// --------------------------------- accessors
	public String[] getSuiteNames() {
		return suiteNames;
	}

	public void setSuiteNames(String[] suiteNames) {
		this.suiteNames = suiteNames;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

}
