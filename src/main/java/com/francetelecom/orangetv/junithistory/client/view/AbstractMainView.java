package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Classe mère pour toutes les vues
 * 
 * @author ndmz2720
 * 
 */
public abstract class AbstractMainView extends AbstractView implements IProfilMainView {

	private final Button btConnection = new Button("Connection");
	private final Label labelProfil = new Label(UserProfile.anybody.name());

	// ------------------------- constructor
	public AbstractMainView(boolean showBtConnection) {
		super(showBtConnection);
		this._initMainComposants(showBtConnection);
	}

	public AbstractMainView() {
		this(true);
	}

	// ------------------------------------------------ public methods
	// -------------------------------------- overriding IView
	@Override
	public void setUserProfil(UserProfile userProfile) {

		if (userProfile != null) {
			this.labelProfil.setText(userProfile.name());
			this.btConnection.setText((userProfile != UserProfile.anybody) ? "Deconnection" : "Connection");
		}
	}

	@Override
	public HasClickHandlers getConnectUserButton() {
		return this.btConnection;
	}

	protected Panel buildPanelConnection() {
		HorizontalPanel panelConnection = new HorizontalPanel();
		panelConnection.setSpacing(PANEL_SPACING);
		panelConnection.add(this.btConnection);
		panelConnection.add(this.labelProfil);
		return panelConnection;
	}

	// ------------------------------ private methods

	protected void _initMainComposants(boolean showBtConnection) {
		getLog().config("_initComposants()");
		this.labelProfil.getElement().setId(ID_PROFIL_USER);
		this.btConnection.setVisible(showBtConnection);
		this.labelProfil.setVisible(showBtConnection);
	}

}
