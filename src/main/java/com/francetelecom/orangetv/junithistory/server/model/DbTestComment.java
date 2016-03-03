package com.francetelecom.orangetv.junithistory.server.model;

import java.util.Date;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.server.util.ListLines;

/**
 * Commentaire ajouté par l'utilisateur suite à l'analyse du problème
 * 
 * @author ndmz2720
 *
 */
public class DbTestComment extends AbstractDbEntry {

	private final Date dateCreation;
	private Date dateModification;
	private final DbTestUser user;
	private String defect;
	private ListLines comment;

	public DbTestComment(Date dateCreation, DbTestUser user) {
		this.dateCreation = dateCreation;
		this.user = user;
	}

}
