package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Value Object of DbTestInstance for list of tests (defect view)
 * 
 * @author NDMZ2720
 *
 */
public class VoTestDistinctName implements IVo {

	private static final long serialVersionUID = 1L;
	private String distinctName;

	// ---------------------------- constructor
	public VoTestDistinctName() {
		super();
	}

	public VoTestDistinctName(String name) {
		this.distinctName = name;
	}

	// ----------------------------- accessors
	public String getDistinctName() {
		return this.distinctName;
	}
}
