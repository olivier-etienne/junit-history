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
	private int testerId;

	private String suiteTitle;
	private String testTitle;

	private String title;
	private String description;

	private boolean readOnly;

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

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public String getSuiteTitle() {
		return suiteTitle;
	}

	public void setSuiteTitle(String suiteTitle) {
		this.suiteTitle = suiteTitle;
	}

	public String getTestTitle() {
		return testTitle;
	}

	public void setTestTitle(String testTitle) {
		this.testTitle = testTitle;
	}

	public void setTestId(int testId) {
		this.testId = testId;
	}

	public int getTesterId() {
		return testerId;
	}

	public void setTesterId(int testerId) {
		this.testerId = testerId;
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
