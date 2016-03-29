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
	private int tclassId;
	private String search;

	// ------------------------------ constructor
	public VoSearchDefectDatas() {
		this(IVo.ID_UNDEFINED, "");
	}

	public VoSearchDefectDatas(int groupId, String search) {
		this(groupId, ID_UNDEFINED, search);
	}

	public VoSearchDefectDatas(int groupId, int tclassId, String search) {
		this.groupId = groupId;
		this.search = search;
		this.tclassId = tclassId;
	}

	// ------------------------------- accessors
	public int getTClassId() {
		return this.tclassId;
	}

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
