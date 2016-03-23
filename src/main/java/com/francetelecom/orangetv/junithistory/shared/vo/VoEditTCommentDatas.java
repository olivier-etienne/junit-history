package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.List;

/**
 * Encapsule les info necessaire a la fenetre d'edition d'un comment de test
 * 
 * @author ndmz2720
 *
 */
public class VoEditTCommentDatas implements IVo {

	private static final long serialVersionUID = 1L;

	private List<VoUser> listUsers;

	private VoTestCommentForEdit commentForEdit;

	// ------------------------------ accessors
	public List<VoUser> getListTesters() {
		return listUsers;
	}

	public void setListTesters(List<VoUser> listUsers) {
		this.listUsers = listUsers;
	}

	public VoTestCommentForEdit getTCommentForEdit() {
		return commentForEdit;
	}

	public void setTCommentForEdit(VoTestCommentForEdit commentForEdit) {
		this.commentForEdit = commentForEdit;
	}

}
