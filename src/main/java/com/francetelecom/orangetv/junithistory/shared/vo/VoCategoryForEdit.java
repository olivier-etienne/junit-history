package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoCategoryForEdit extends AbstractVoIdName {

	private static final long serialVersionUID = 1L;

	private String listClassNames;

	// ------------------------------- constructor
	public VoCategoryForEdit() {
	}

	public VoCategoryForEdit(int id, String name) {
		super(id, name);
	}

	// ---------------------------- accessors
	public void setName(String name) {
		super.setName(name);
	}

	public String getListClassNames() {
		return listClassNames == null ? "" : this.listClassNames;
	}

	public void setListClassNames(String listClassNames) {
		this.listClassNames = listClassNames;
	}

}
