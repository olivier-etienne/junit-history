package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;

public interface IProfilMainPresenter extends IMainPresenter {

	public void manageUserProfil(UserProfile userProfile);

	public IProfilMainView getProfilMainView();
}
