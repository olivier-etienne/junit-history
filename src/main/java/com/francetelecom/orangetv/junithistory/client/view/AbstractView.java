package com.francetelecom.orangetv.junithistory.client.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.AbstractMainPresenter;
import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.client.util.StatusUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.AbstractVoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractView extends Composite implements IView, CssConstants {

	protected final VerticalPanel main = new VerticalPanel();
	private final FlowPanel panelButton = new FlowPanel();
	protected final Label labelResult = new Label();
	protected final Label labelTitle = new Label("");

	protected final DateTimeFormat DF = AbstractMainPresenter.DF;

	private final Map<String, ButtonViewAction> mapAction2ButtonViewActions = new HashMap<>();

	// ------------------------- constructor
	public AbstractView(boolean showBtConnection) {
		this._initComposants(showBtConnection);
	}

	public AbstractView() {
		this(true);
	}

	// ------------------------------------------------ public methods
	// -------------------------------------- overriding IView

	/**
	 * Tous les boutons sont inactives sauf ceux fourni par le tableau actions
	 * Seuls les boutons actifs sont manipulables par l'utilisateur
	 * 
	 * @param actif
	 * @param actions
	 *            à activer
	 */
	@Override
	public void activeButtons(String... actions) {

		this.inactifAllButtons();

		if (actions == null) {
			return;
		}
		for (String action : actions) {

			ButtonViewAction button = this.mapAction2ButtonViewActions.get(action);
			if (button != null) {
				button.setActif(true);
			}
		}
	}

	@Override
	public void activeAllButtons() {
		for (ButtonViewAction button : this.mapAction2ButtonViewActions.values()) {
			button.setActif(true);
		}
	}

	private void inactifAllButtons() {

		for (ButtonViewAction button : this.mapAction2ButtonViewActions.values()) {
			button.setActif(false);
		}
	}

	@Override
	public void selectButton(ButtonViewAction button) {
		for (ButtonViewAction buttonViewAction : mapAction2ButtonViewActions.values()) {
			buttonViewAction.setSelected(button == buttonViewAction);
		}
	}

	@Override
	public void waiting(boolean waiting) {
		if (waiting) {
			this.lock();
			this.main.addStyleName(STYLE_CURSOR_WAIT);
		} else {
			this.unlock();
			this.main.removeStyleName(STYLE_CURSOR_WAIT);
		}
	}

	@Override
	public void setViewActionClickHandler(ClickHandler clickHandler) {
		this.setButtonClickHandler(clickHandler);
	}

	@Override
	public void setActionResult(String text, LogStatus logStatus) {

		this.labelResult.getElement().setInnerHTML(text);
		StatusUtils.buildLogLabel(this.labelResult, logStatus);
		this.labelResult.setVisible(text != null);
	}

	// ------------------------------------ overriding WIdget
	@Override
	public Widget asWidget() {
		return this;
	}

	// ------------------------------------------------ protected methods

	protected void init(String title) {
		this.initComposants();
		this.initHandlers();
		this.initWidget(this.buildMainPanel(title));
	}

	protected Panel buildPanelConnection() {
		return null;
	}

	protected Widget buildMainPanel(String title) {

		HorizontalPanel panelTop = new HorizontalPanel();
		panelTop.setSpacing(PANEL_SPACING);
		panelTop.setWidth(MAX_WIDTH);
		panelTop.addStyleName(TITLE_PANEL_VIEW);

		this.labelTitle.setText(title);
		labelTitle.addStyleName(TITLE_LABEL_VIEW);
		panelTop.add(labelTitle);
		panelTop.setCellHorizontalAlignment(labelTitle, HasHorizontalAlignment.ALIGN_CENTER);

		Panel panelConnection = this.buildPanelConnection();
		if (panelConnection != null) {
			panelTop.add(panelConnection);
			panelTop.setCellHorizontalAlignment(panelConnection, HasHorizontalAlignment.ALIGN_RIGHT);
		}

		this.main.setSpacing(PANEL_SPACING);
		this.main.setWidth(MAX_WIDTH);
		this.main.setHeight(MAX_WIDTH);

		this.main.add(panelTop);

		this.buildBodyPanel();

		this.buildButtonPanel();

		FlowPanel panelBottom = new FlowPanel();
		panelBottom.addStyleName(PANEL_VIEW_BUTTON);
		panelBottom.add(this.panelButton);
		panelBottom.add(this.labelResult);
		this.main.add(panelBottom);

		return this.main;
	}

	protected abstract Logger getLog();

	protected abstract void buildBodyPanel();

	protected abstract void buildButtonPanel();

	protected abstract void initHandlers();

	protected abstract void initComposants();

	protected void addButton(ButtonViewAction button) {
		this.mapAction2ButtonViewActions.put(button.getViewAction(), button);
		this.panelButton.add(button);
	}

	protected void populateList(ListBox listBox, List<?> datas) {

		listBox.addItem("undefined", IVo.ID_UNDEFINED + "");
		if (datas != null && !datas.isEmpty()) {

			Object data = datas.get(0);
			if (data instanceof AbstractVoIdName) {

				for (Object obj : datas) {

					AbstractVoIdName voIdName = (AbstractVoIdName) obj;
					listBox.addItem(voIdName.getName(), voIdName.getId() + "");
				}
			}

		}

	}

	// ------------------------------ private methods

	protected void _initComposants(boolean showBtConnection) {
		getLog().config("_initComposants()");
		this.panelButton.setWidth(MAX_WIDTH);
		this.labelResult.setVisible(false);
		this.labelResult.addStyleName(MARGIN_LEFT_10);

	}

	private void setButtonClickHandler(ClickHandler handler) {
		for (ButtonViewAction buttonViewAction : mapAction2ButtonViewActions.values()) {
			buttonViewAction.addClickHandler(handler);
		}
	}

	// ================================ INNER CLASS
	public class ButtonViewAction extends Button {

		// actif: controle fonctionnel du bouton
		// on ne peut pas enabler ou disabler un bouton inactif
		// lié a un etat fonctionnel
		private boolean actif = true;

		// action dans le contexte de la vue/presenter
		private final String viewAction;

		public String getViewAction() {
			return this.viewAction;
		}

		private void setActif(boolean actif) {
			this.actif = actif;
			this.setEnabled(actif);
		}

		protected void enableButtonIfActif(boolean enabled) {

			if (this.actif) {
				this.setEnabled(enabled);
			}
		}

		protected void setSelected(boolean selected) {

			if (selected) {
				this.addStyleName(STYLE_SELECTED);
			} else {
				this.removeStyleName(STYLE_SELECTED);
			}
		}

		public ButtonViewAction(String text, String viewAction, String title) {
			super(text);
			this.viewAction = viewAction;
			super.setTitle(title);
			this.setStyleName(BUTTON_VIEW_ACTION);
		}
	}

	// ================================= INNER CLASS
	public static class HeaderLabel extends Label {

		private final boolean center;

		public boolean isCentred() {
			return this.center;
		}

		public HeaderLabel(String text, boolean center) {
			super(text);
			this.center = center;
			String style = GRID_SUITE_HEADER + " " + (center ? TEXT_ALIGN_CENTER : TEXT_ALIGN_LEFT);
			this.addStyleName(style);
		}

		public HeaderLabel(String text) {
			this(text, false);
		}
	}

	public static class ValueLabel extends Label {

		public ValueLabel(String text) {
			this(text, false);
		}

		public ValueLabel(String text, boolean center) {
			super(text);
			String style = GRID_SUITE_LABEL + (center ? " " + TEXT_ALIGN_CENTER : "");
			super.addStyleName(style);
			this.setTitle(text);
		}

		public ValueLabel(int value, boolean center) {
			this(value + "", center);
		}

		public ValueLabel(int value) {
			this(value + "");
		}
	}

}
