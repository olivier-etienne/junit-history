package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tests for one STB group and search input
 * 
 * @author NDMZ2720
 *
 */
public class VoResultSearchTestDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoTestDistinctName> listTests = new ArrayList<>();

	// -------------------------------- constructor
	public VoResultSearchTestDatas() {
	}

	// --------------------------------- public methods
	public void addTest(VoTestDistinctName vo) {
		this.listTests.add(vo);
	}

	public List<VoTestDistinctName> getListTests() {
		return this.listTests;
	}

}
