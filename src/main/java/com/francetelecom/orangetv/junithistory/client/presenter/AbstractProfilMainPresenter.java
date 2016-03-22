package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractProfilMainPresenter extends AbstractPresenter implements IProfilMainPresenter {

	public static final DateTimeFormat DF = DateTimeFormat.getFormat("dd MMM yyyy");

	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	protected static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network " + "connection and try again.";

	// ----------------------------- abstract methods

	// ------------------------------- constructor
	protected AbstractProfilMainPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus) {
		super(service, eventBus);
	}

	// ------------------------------ implemnenting IPresenterAndProfile
	public IProfilMainView getProfilMainView() {
		return (IProfilMainView) this.getView();
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
		this.getProfilMainView().setUserProfil(userProfile);
	}

}