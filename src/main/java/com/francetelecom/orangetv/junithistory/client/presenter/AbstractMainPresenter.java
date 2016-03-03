package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractMainPresenter extends AbstractPresenter
		implements IMainPresenter {

	public static final DateTimeFormat DF = DateTimeFormat
			.getFormat("dd MMM yyyy");

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	protected static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	// ----------------------------- abstract methods

	// ------------------------------- constructor
	protected AbstractMainPresenter(IGwtJUnitHistoryServiceAsync service,
			EventBus eventBus) {
		super(service, eventBus);
	}

	// ------------------------------ implemnenting IPresenterAndProfile
	public IMainView getMainView() {
		return (IMainView) this.getView();
	}

	// ----------------------------- implementing IPresenter
	@Override
	public boolean hasUserProfilToManage() {
		// default
		return true;
	}

	@Override
	public void manageUserProfil(UserProfile userProfile) {
		getLog().config("manageUserProfil(): " + userProfile);
		this.getMainView().setUserProfil(userProfile);
	}

}
