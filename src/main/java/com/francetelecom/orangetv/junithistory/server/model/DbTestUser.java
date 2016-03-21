package com.francetelecom.orangetv.junithistory.server.model;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;

public class DbTestUser extends AbstractDbEntry {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;

	private boolean admin = false;

	// ------------------------------- constructor
	protected DbTestUser() {
	}

	public DbTestUser(String name) {
		this.name = name;
	}

	public DbTestUser(int id) {
		this.setId(id);
	}

	// ------------------------------ accessors

	public String getName() {
		return this.name;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	// ------------------------------ public methods
	public VoUser toVo() {

		return new VoUser(this.getId(), this.name);
	}

	public void update(VoUserForEdit voUser) {

		if (voUser != null) {
			this.name = voUser.getName();
			this.description = voUser.getDescription();
		}
	}

	// --------------------------- overriding Object

}
