package com.francetelecom.orangetv.junithistory.client.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent;
import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class PanelMenu extends Composite implements CssConstants, IPanel {

	private final static Logger log = Logger.getLogger("PanelMenu");

	private final Panel main = new SimplePanel();

	private final MenuButton btSingleReport = new MenuButton("Single report", MainPanelViewEnum.singleReport);
	private final MenuButton btHistoricReports = new MenuButton("History reports", MainPanelViewEnum.historicReport);

	private final MenuButton btPageAdmin = new MenuButton("Page admin", MainPanelViewEnum.admin, UserProfile.admin);
	private final MenuButton btAnalysis = new MenuButton("Analysis", MainPanelViewEnum.analysis);

	private final List<MenuButton> listMenuButtons = new ArrayList<>();

	private ClickHandler buttonClickHandler;

	private EventBus eventBus;

	// ------------------------- constructor
	public PanelMenu() {

		this.initComposants();

		this.initWidget(this.buildMainPanel());
		this.initHandlers();
		this.btSingleReport.setSelected(true);

	}

	// -------------------------------- implementing IPanel
	@Override
	public void lock() {
		this.setEnabled(false);
	}

	@Override
	public void unlock() {
		this.setEnabled(true);
	}

	// -------------------------------- public methods
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void selectButton(MainPanelViewEnum viewEnum) {
		if (this.listMenuButtons == null) {
			return;
		}
		for (MenuButton button : listMenuButtons) {
			button.setSelected(button.viewEnum == viewEnum);
		}

	}

	public void manageUserProfil(UserProfile userProfile) {
		log.config("manageUserProfil(): " + userProfile.name());
		if (this.listMenuButtons == null) {
			return;
		}
		for (MenuButton button : listMenuButtons) {

			button.setActif(button.isActivableForProfile(userProfile));
		}

	}

	// ------------------------------ private methods
	private void initComposants() {
	}

	private void setEnabled(boolean enabled) {
		if (this.listMenuButtons == null) {
			return;
		}
		for (MenuButton actionButton : listMenuButtons) {
			actionButton.enableButtonIfActif(enabled);
		}
	}

	private Widget buildMainPanel() {

		this.listMenuButtons.add(this.btSingleReport);
		this.listMenuButtons.add(this.btHistoricReports);
		this.listMenuButtons.add(this.btAnalysis);
		this.listMenuButtons.add(this.btPageAdmin);

		this.main.addStyleName(PANEL_MENU);

		VerticalPanel vpButton = new VerticalPanel();
		vpButton.setSpacing(PANEL_SPACING);

		for (MenuButton actionButton : this.listMenuButtons) {
			vpButton.add(actionButton);
		}

		this.main.add(vpButton);
		return this.main;
	}

	private void initHandlers() {

		this.buttonClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source != null && source instanceof MenuButton) {
					MenuButton button = (MenuButton) source;
					selectButton(button.viewEnum);
					onSelectAction(button.viewEnum);
				}
			}
		};

		if (this.listMenuButtons != null) {
			for (MenuButton actionButton : listMenuButtons) {
				actionButton.addClickHandler(buttonClickHandler);
			}
		}
	}

	private void onSelectAction(MainPanelViewEnum viewEnum) {

		if (this.eventBus != null) {
			this.eventBus.fireEvent(new ViewReportEvent(viewEnum));
		}
	}

	// ================================ INNER CLASS
	private class MenuButton extends Button {

		private final MainPanelViewEnum viewEnum;
		private final UserProfile minProfile;

		// actif: controle fonctionnel du bouton
		// on ne peut pas enabler ou disabler un bouton inactif
		// lié a un etat fonctionnel
		private boolean actif = true;

		private void setActif(boolean actif) {
			this.actif = actif;
			this.setEnabled(actif);
		}

		private void enableButtonIfActif(boolean enabled) {

			if (this.actif) {
				this.setEnabled(enabled);
			}
		}

		private void setSelected(boolean selected) {

			if (selected) {
				this.addStyleName(STYLE_SELECTED);
			} else {
				this.removeStyleName(STYLE_SELECTED);
			}
		}

		private boolean isActivableForProfile(UserProfile currentProfile) {
			return currentProfile.ordinal() <= this.minProfile.ordinal();
		}

		private MenuButton(String text, MainPanelViewEnum viewEnum, UserProfile minProfile) {
			super(text);
			this.viewEnum = viewEnum;
			this.minProfile = (minProfile == null) ? UserProfile.anybody : minProfile;
			this.setStyleName(BUTTON_MENU_STYLE);

		}

		private MenuButton(String text, MainPanelViewEnum viewEnum) {
			this(text, viewEnum, null);
		}
	}

}
