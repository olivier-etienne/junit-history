package com.francetelecom.orangetv.junithistory.server.model;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupName;

/**
 * Categorie dans laquelle sont groupées les suites de tests correspondant à une
 * meme STB
 * 
 * @author ndmz2720
 * 
 */
public class DbTestSuiteGroup extends AbstractDbEntry {
	private static final long serialVersionUID = 1L;

	private String stb;
	private String name;
	private String prefix;

	// ------------------------- constructor
	public DbTestSuiteGroup(String stb, String name, String prefix) {
		this.stb = stb;
		this.name = name;
		this.prefix = prefix;
	}

	// ------------------------- accessors
	public String getStb() {
		return stb;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	// ------------------------------ public methods
	public VoGroupName toVo() {

		return new VoGroupName(this.getId(), this.name);
	}

	public void update(VoGroupForEdit voGroup) {

		if (voGroup != null) {
			this.name = voGroup.getName();
			this.stb = voGroup.getStb();
			this.prefix = voGroup.getPrefix();
		}
	}

	// ------------------------------ overriding Object
	@Override
	public String toString() {

		return this.getId() + " - " + this.stb + " - " + this.name + " - " + this.prefix;
	}

}
