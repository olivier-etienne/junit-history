package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsule une liste de tests de meme nom pour un STB group
 * et sa liste de message et comment
 * 
 * @author NDMZ2720
 *
 */
public class VoListTestsSameNameDatas implements IVo {

	private static final long serialVersionUID = 1L;

	// nom commun à tous les tests
	private String distinctName;

	private List<VoTestInstanceForEdit> listTestSameName;

	// ---------------------------- constructor
	public VoListTestsSameNameDatas() {
	}

	public VoListTestsSameNameDatas(String name) {
		this.distinctName = name;
	}

	// ----------------------- accessors
	public String getDistinctName() {
		return this.distinctName;
	}

	public List<VoTestInstanceForEdit> getListTestsSameName() {
		if (this.listTestSameName == null) {
			this.listTestSameName = new ArrayList<>();
		}
		return this.listTestSameName;
	}

	public void addTestInstance(VoTestInstanceForEdit voTest) {
		this.getListTestsSameName().add(voTest);
	}

}
