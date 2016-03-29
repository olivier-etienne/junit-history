package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * Value object for init DefecttView
 * 
 * @author ndmz2720
 *
 */
public class VoInitDefectDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoIdName> listGroups = new ArrayList<>();

	// --------------------------------- accessors
	public List<VoIdName> getListGroups() {
		return listGroups;
	}

}
