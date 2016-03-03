package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.List;

/**
 * Encapsule les info necessaire a la fenetre d'edition d'une suite
 * 
 * @author ndmz2720
 *
 */
public class VoEditReportDatas implements IVo {
	private static final long serialVersionUID = 1L;

	private List<VoUser> listUsers;

	private VoTestSuiteForEdit suiteForEdit;

	// ------------------------------ accessors
	public List<VoUser> getListUsers() {
		return listUsers;
	}

	public void setListUsers(List<VoUser> listUsers) {
		this.listUsers = listUsers;
	}

	public VoTestSuiteForEdit getSuiteForEdit() {
		return suiteForEdit;
	}

	public void setSuiteForEdit(VoTestSuiteForEdit suiteForEdit) {
		this.suiteForEdit = suiteForEdit;
	}

}
