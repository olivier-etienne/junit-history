package com.francetelecom.orangetv.junithistory.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent;
import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent.ViewReportHandler;
import com.francetelecom.orangetv.junithistory.client.panel.PanelConnection;
import com.francetelecom.orangetv.junithistory.client.panel.PanelConnection.Credential;
import com.francetelecom.orangetv.junithistory.client.panel.PanelMenu;
import com.francetelecom.orangetv.junithistory.client.presenter.AbstractProfilMainPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactory;
import com.francetelecom.orangetv.junithistory.client.presenter.EditReportPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.EditTCommentPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.IMainPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.IProfilMainPresenter;
import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils.MyDialogBox;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils.MyDialogView;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class AppController extends AbstractProfilMainPresenter implements ValueChangeHandler<String> {

	private final static Logger log = Logger.getLogger("AppController");

	private static final int PROFILE_REFRESH = 1000 * 60 * 2; // 1000 * 60 * 15;
																// 15mn

	private final ClientFactory clientFactory;

	private final Panel main = new FlowPanel();
	private final PanelMenu panelMenu = new PanelMenu();
	private final SimplePanel panelView = new SimplePanel();

	public enum MainPanelViewEnum {

		singleReport, historicReport, editReport, admin, analysis, editComment
	}

	private ClickHandler connectUseClickHandler;

	private UserProfile currentUserProfile;
	private IMainPresenter currentPresenter;

	// ----------------------------------- constructor
	public AppController(ClientFactory clientFactory) {

		super(clientFactory.getService(), clientFactory.getEventBus());
		this.clientFactory = clientFactory;
		bind();
	}

	// ------------------------------ overriding ValueChangeHandler
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {

		String token = event.getValue();
		if (token != null) {

			MainPanelViewEnum viewEnum = MainPanelViewEnum.valueOf(token);
			if (viewEnum != null) {
				this.fireEventToView(viewEnum, null, false);
			}
		}

	}

	// ------------------------------ overriding IPresenter
	@Override
	public IView getView() {
		return (this.currentPresenter == null) ? null : this.currentPresenter.getView();
	}

	// ---------------------------- overriding AbstractPresenter
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return null;
	}

	// ----------------------------------------------------overriding
	// AbstractPresenter
	@Override
	public void go(HasWidgets container) {

		log.info("go(container)");
		container.add(this.buildMainPanel());

		if ("".equals(History.getToken())) {
			History.newItem(MainPanelViewEnum.singleReport.name());
		} else {
			History.fireCurrentHistoryState();
		}

		this.doGetCurrentUserProfil();
		new Timer() {

			@Override
			public void run() {
				doGetCurrentUserProfil();

			}
		}.scheduleRepeating(PROFILE_REFRESH);

	}

	@Override
	protected void loadDatas(boolean forceRefresh) {
		// nothing

	}

	// ------------------------------------------------- private methods
	private Panel buildMainPanel() {

		this.main.add(this.panelMenu);

		this.panelView.addStyleName(PANEL_VIEW);
		this.main.add(this.panelView);

		return this.main;
	}

	private void bind() {

		History.addValueChangeHandler(this);
		this.eventBus.addHandler(ViewReportEvent.TYPE, new ViewReportHandler() {

			public void onChangeView(ViewReportEvent event) {

				MainPanelViewEnum viewEnum = (event != null) ? event.getMainPanelViewEnum() : null;

				if (viewEnum != null) {

					log.info("onChangeView(): " + viewEnum.name());
					panelMenu.selectButton(viewEnum);

					boolean manageUserProfile = true;
					switch (viewEnum) {
					case singleReport:
						diplayView(MainPanelViewEnum.singleReport, event.getParams(), false, panelView);
						break;

					case historicReport:
						diplayView(MainPanelViewEnum.historicReport, event.getParams(), false, panelView);
						break;

					case editReport:
						if (currentPresenter != null) {
							displayEditReport(event.getParams());
							manageUserProfile = false;
						} else {
							History.back();
						}
						break;

					case editComment:
						if (currentPresenter != null) {
							displayEditTComment(event.getParams());
							manageUserProfile = false;
						} else {
							History.back();
						}

					case admin:
						diplayView(MainPanelViewEnum.admin, event.getParams(), true, panelView);
						break;

					case analysis:
						diplayView(MainPanelViewEnum.analysis, event.getParams(), false, panelView);
						break;
					}
					if (manageUserProfile) {
						manageUserProfil();
					}
				}

			}

		});

		this.panelMenu.setEventBus(this.eventBus);

	}

	private ClickHandler getConnectUserClickHandler() {

		if (this.connectUseClickHandler == null) {
			this.connectUseClickHandler = new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					showDialogConnection();
				}
			};
		}
		return this.connectUseClickHandler;
	}

	/*
	 * Creation generique vue et presenter
	 */
	private void diplayView(MainPanelViewEnum viewEnum, Map<String, Object> params, boolean adminOnly,
			HasWidgets container) {

		if (adminOnly) {
			// on verifie que le user est bien admin
			if (this.currentUserProfile == null || this.currentUserProfile != UserProfile.admin) {
				History.back();
				this.getView().setActionResult("User is not admin!", LogStatus.warning);
				return;
			}
		}
		log.info("displayView() " + viewEnum.name());

		History.newItem(viewEnum.name(), false);

		IMainView view = this.clientFactory.getMainView(viewEnum);
		if (view != null && view instanceof IProfilMainView) {
			((IProfilMainView) view).getConnectUserButton().addClickHandler(this.getConnectUserClickHandler());
		}

		IMainPresenter presenter = this.clientFactory.getMainPresenter(viewEnum);
		if (presenter == null) {

			presenter = this.clientFactory.buildMainPresenter(view);
		}

		this.currentPresenter = presenter;
		this.currentPresenter.go(container, params);

	}

	private void displayEditReport(final Map<String, Object> params) {

		final MyDialogView container = WidgetUtils.buildDialogView("");
		this.diplayView(MainPanelViewEnum.editReport, params, false, container);

		((EditReportPresenter) this.currentPresenter).setCloseDialogClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				container.hide();

				// on revient à l'historique et on force le
				// rafraichissement
				fireEventToView(MainPanelViewEnum.historicReport, params, true);
			}
		});

		WidgetUtils.centerDialogAndShow(container);
	}

	private void displayEditTComment(final Map<String, Object> params) {

		final MyDialogView container = WidgetUtils.buildDialogView("");
		this.diplayView(MainPanelViewEnum.editComment, params, false, container);

		((EditTCommentPresenter) this.currentPresenter).setCloseDialogClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				container.hide();

				// on revient à la page analyse et on force le
				// rafraichissement
				log.config("Dialog.onClose() >> display analysis and force refresh!");
				fireEventToView(MainPanelViewEnum.analysis, params, true);
			}
		});

		WidgetUtils.centerDialogAndShow(container);
	}

	private void fireEventToView(MainPanelViewEnum viewEnum, Map<String, Object> params, boolean forceRefresh) {

		log.config("fireEventToView(): " + viewEnum.name() + " - " + forceRefresh
				+ (params != null ? " with param" : ""));
		ViewReportEvent viewEvent = new ViewReportEvent(viewEnum);

		if (forceRefresh) {
			if (params == null) {
				params = new HashMap<String, Object>(0);
			}
			params.put(PARAMS_FORCE_REFRESH, true);
		}
		viewEvent.setParams(params);
		this.eventBus.fireEvent(viewEvent);

	}

	private void showDialogConnection() {

		if (this.currentUserProfile != UserProfile.anybody) {
			this.doAuthenticateUser(null);
		} else { // connection
			final PanelConnection connectionPanel = new PanelConnection();
			final MyDialogBox dialogBox = WidgetUtils.buildDialogBox("Identification", null, connectionPanel, true,
					true, false, new IActionCallback() {

						@Override
						public void onOk() {
							doAuthenticateUser(connectionPanel.getDataFromWidget());
						}

						@Override
						public void onCancel() {
							setActionResult("Identification canceled!", LogStatus.warning);
						}
					});

			WidgetUtils.centerDialogAndShow(dialogBox);
		}
	}

	private void doAuthenticateUser(final Credential credential) {

		// si credential null, authentification renverra une connection anonyme
		// equivalente à une deconnection
		String login = (credential == null) ? "" : credential.getLogin();
		String pwd = (credential == null) ? "" : credential.getPwd();

		this.rpcService.authenticateUserProfile(login, pwd, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				setActionResult("Authentication failed!", LogStatus.warning);
			}

			@Override
			public void onSuccess(String newProfile) {

				// manage profile
				manageUserProfil(newProfile);
			}
		});
	}

	public void setActionResult(String text, LogStatus logStatus) {

		IMainView view = this.getProfilMainView();
		if (view != null) {
			view.setActionResult(text, logStatus);
		}
	}

	private void doGetCurrentUserProfil() {
		this.rpcService.getCurrentUserProfile(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				setActionResult("Error getting user profile!", LogStatus.error);
			}

			@Override
			public void onSuccess(String newProfile) {
				manageUserProfil(newProfile);
			}
		});
	}

	/*
	 * Appelle - apres authentification or - apres un getCurrentUserProfile()
	 */
	private void manageUserProfil(String newProfile) {

		log.fine("UserProfile: " + newProfile);
		UserProfile profile = UserProfile.valueOf(newProfile);
		if (profile != null && profile != currentUserProfile) {
			log.info("change profile from " + currentUserProfile + " to " + profile);
			currentUserProfile = profile;
			manageUserProfil();
		}
	}

	/*
	 * Si le profile a change on repercute sur le presenter et le menu
	 */
	private void manageUserProfil() {
		if (this.currentPresenter != null && this.currentUserProfile != null) {

			if (this.currentPresenter instanceof IProfilMainPresenter) {

				IProfilMainPresenter profilMainPresenter = (IProfilMainPresenter) this.currentPresenter;
				profilMainPresenter.manageUserProfil(this.currentUserProfile);
			}
			this.panelMenu.manageUserProfil(this.currentUserProfile);
		}
	}

}
