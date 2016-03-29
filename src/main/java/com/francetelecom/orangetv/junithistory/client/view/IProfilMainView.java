package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Contrat d'interface pour toutes les vues
 * 
 * @author ndmz2720
 *
 */
public interface IProfilMainView extends IMainView {

	public HasClickHandlers getConnectUserButton();

	public void setUserProfil(UserProfile userProfile);

}
