package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object for init SingleReportView
 * 
 * @author ndmz2720
 *
 */
public class VoInitSingleReportDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoGroupName> listGroups = new ArrayList<>();
	private List<VoUser> listUsers = new ArrayList<>();

	private VoSingleReportProtection protection;

	// --------------------------------- accessors
	public List<VoGroupName> getListGroups() {
		return listGroups;
	}

	public List<VoUser> getListUsers() {
		return listUsers;
	}

	public VoSingleReportProtection getProtection() {
		return protection;
	}

	public void setProtection(VoSingleReportProtection protection) {
		this.protection = protection;
	}

}
