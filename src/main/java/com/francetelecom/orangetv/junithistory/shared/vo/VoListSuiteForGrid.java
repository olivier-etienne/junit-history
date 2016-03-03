package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

public class VoListSuiteForGrid implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoTestSuiteForGrid> listTestSuites = new ArrayList<>(0);

	private VoItemProtection protection;

	// url permettant un access direct sur les pages html du group sans passer
	// par l'ihm
	private String urlToShare;

	public String getUrlToShare() {
		return urlToShare;
	}

	public void setUrlToShare(String urlToShare) {
		this.urlToShare = urlToShare;
	}

	public List<VoTestSuiteForGrid> getListTestSuites() {
		return listTestSuites;
	}

	public void setListTestSuites(List<VoTestSuiteForGrid> listTestSuites) {
		this.listTestSuites = listTestSuites;
	}

	public VoItemProtection getProtection() {
		return protection;
	}

	public void setProtection(VoItemProtection protection) {
		this.protection = protection;
	}

}
