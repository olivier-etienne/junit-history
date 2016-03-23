package com.francetelecom.orangetv.junithistory.server.model;

import java.util.Date;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;

/**
 * Commentaire ajouté par l'utilisateur suite à l'analyse du problème
 * 
 * @author ndmz2720
 *
 */
public class DbTestComment extends AbstractDbEntry {

	private static final long serialVersionUID = 1L;

	private Date dateCreation; // required
	private Date dateModification; // required

	private DbTestUser tester; // required lazy

	private String title; // required
	private String description; // required

	// ------------------------------------------- constructor
	protected DbTestComment() {
	}

	public DbTestComment(Date dateCreation, DbTestUser user) {
		this.dateCreation = dateCreation;
		this.tester = user;
	}

	// --------------------------------------- accessor

	public Date getDateModification() {
		return dateModification;
	}

	public void setTester(DbTestUser tester) {
		this.tester = tester;
	}

	public void setDateModification(Date dateModification) {
		this.dateModification = dateModification;
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

	public Date getDateCreation() {
		return dateCreation;
	}

	public DbTestUser getTester() {
		return tester;
	}

	// ------------------------------- public methods
	public void update(VoTestCommentForEdit vo) {

		this.title = vo.getTitle();
		this.description = vo.getDescription();

		this.tester = new LazyTestUser(vo.getTesterId());
	}
}
