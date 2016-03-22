package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;

public interface IProfilMainPresenter extends IPresenter {

	public void manageUserProfil(UserProfile userProfile);

	public boolean hasUserProfilToManage();

	public IProfilMainView getProfilMainView();
}
