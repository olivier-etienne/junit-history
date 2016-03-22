package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/***
 * Presenter pour l'edition ou la creation d'un commentaire de test
 */
public class EditTCommentPresenter extends AbstractPresenter {

	private final static Logger log = Logger.getLogger("EditTCommentPresenter");

	private ITCommentEditView view;

	// ------------------------------------------- constructor
	public EditTCommentPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus) {
		super(service, eventBus);
	}

	// ------------------------------- Implementing IPresenter
	@Override
	public IView getView() {
		return this.view;
	}

	// ------------------------------- overriding AbstractPresenter
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view == null ? null : this.view.asWidget();
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {
		// TODO Auto-generated method stub

	}

	// ================================= VIEW
	public interface ITCommentEditView extends IView {

	}

}
