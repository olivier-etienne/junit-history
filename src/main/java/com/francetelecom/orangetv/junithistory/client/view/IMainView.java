package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Contrat d'interface pour toutes les vues
 * 
 * @author ndmz2720
 *
 */
public interface IMainView extends IView {

	public HasClickHandlers getConnectUserButton();

	public void setUserProfil(UserProfile userProfile);

	public MainPanelViewEnum getViewType();

}
