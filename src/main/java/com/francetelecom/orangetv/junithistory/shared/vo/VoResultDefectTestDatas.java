package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tests for one STB group and search input
 * 
 * @author NDMZ2720
 *
 */
public class VoResultDefectTestDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoTestInstanceForList> listTests = new ArrayList<>();

	// -------------------------------- constructor
	public VoResultDefectTestDatas() {
	}

	// --------------------------------- public methods
	public void addTest(VoTestInstanceForList vo) {
		this.listTests.add(vo);
	}

	public List<VoTestInstanceForList> getListTests() {
		return this.listTests;
	}

}
