package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.shared.UserProfile;

public interface IPresenterAndProfil extends IPresenter {

	public void manageUserProfil(UserProfile userProfile);

	public boolean hasUserProfilToManage();
}
