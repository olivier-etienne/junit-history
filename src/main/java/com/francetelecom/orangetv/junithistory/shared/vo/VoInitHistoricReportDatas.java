package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object for init HistoricReportView
 * 
 * @author ndmz2720
 *
 */
public class VoInitHistoricReportDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoGroupName> listGroups = new ArrayList<>();

	// --------------------------------- accessors
	public List<VoGroupName> getListGroups() {
		return listGroups;
	}

}
