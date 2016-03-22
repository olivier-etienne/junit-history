package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.EditTCommentPresenter.ITCommentEditView;

/**
 * View pour l'edition ou la creation d'un commentaire de test
 * 
 * @author ndmz2720
 *
 */
public class EditTCommentView extends AbstractEditView implements ITCommentEditView {

	private final static Logger log = Logger.getLogger("EditTCommentView");

	protected EditTCommentView() {
		super("Comment");
		// TODO Auto-generated constructor stub
	}

	// -------------------------- implementing IView
	@Override
	public void reinit() {
		// TODO Auto-generated method stub

	}

	// -------------------------- implementing AbstractView
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {
		// TODO Auto-generated method stub

	}

}
