package com.francetelecom.orangetv.junithistory.shared.vo;

/**
 * Vo pour la creation ou l'edition d'un tcomment
 * 
 * @author ndmz2720
 *
 */
public class VoTestCommentForEdit extends AbstractVoId implements IVoId {
	private static final long serialVersionUID = 1L;

	private int testId;
	private int userId;

	private String title;
	private String description;

	// ---------------------------- constructor
	public VoTestCommentForEdit() {
		super();
	}

	public VoTestCommentForEdit(int id) {
		super(id);
	}

	// ---------------------------- accessors
	public int getTestId() {
		return testId;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
