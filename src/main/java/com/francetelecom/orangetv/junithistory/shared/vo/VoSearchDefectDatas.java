package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Value object for DefectView Datas
 * 
 * @author NDMZ2720
 *
 */
public class VoSearchDefectDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private int groupId;
	private String search;

	// ------------------------------ constructor
	public VoSearchDefectDatas() {
		this(IVo.ID_UNDEFINED, "");
	}

	public VoSearchDefectDatas(int groupId, String search) {
		this.groupId = groupId;
		this.search = search;
	}

	// ------------------------------- accessors
	public int getGroupId() {
		return groupId;
	}

	public String getSearch() {
		return search;
	}

	@Override
	public String toString() {

		return this.groupId + " - " + this.search;
	}

}
