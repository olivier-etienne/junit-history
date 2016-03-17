package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Value Object of DbTestInstance for list of tests (defect view)
 * 
 * @author NDMZ2720
 *
 */
public class VoTestInstanceForList extends AbstractVoIdName {

	private static final long serialVersionUID = 1L;

	// ---------------------------- constructor
	public VoTestInstanceForList() {
		super();
	}

	public VoTestInstanceForList(int id, String name) {
		super(id, name);
	}

}
