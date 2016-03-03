package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;

public interface IMainPresenter extends IPresenter {

	public void manageUserProfil(UserProfile userProfile);

	public boolean hasUserProfilToManage();

	public IMainView getMainView();
}
