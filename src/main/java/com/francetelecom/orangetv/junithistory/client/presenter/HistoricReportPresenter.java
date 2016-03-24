package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent;
import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView.ButtonViewAction;
import com.francetelecom.orangetv.junithistory.client.view.HistoricReportView.GridActionButton;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitHistoricReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListSuiteForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForGrid;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class HistoricReportPresenter extends AbstractProfilMainPresenter {

	private final static Logger log = Logger.getLogger("HistoricReportPresenter");

	public enum ViewActionEnum {
		showGroupReport, showAllReport, buildUrl;
	}

	public enum GridActionButtonEnum {
		show, delete, edit, url;

	}

	private final IHistoricReportView view;

	private Map<Integer, VoIdName> mapId2Groups = new HashMap<>(0);
	private Map<Integer, VoTestSuiteForGrid> mapId2TestSuites = new HashMap<>(0);

	private String urlToShare;
	private final String[] no_action = null;

	// -------------------------------------- implementing IPresenter

	@Override
	public IMainView getView() {
		return this.view;
	}

	// ------------------------------------- overriding AbstractPresenter
	/*
	 * Appelle a chaque go()
	 */
	@Override
	protected void loadDatas(boolean forceRefresh) {

		int groupId = this.getCurrentGroupFromParamsOrView();

		if (forceRefresh || groupId != this.view.getCurrentGroupId()) {
			this.view.setCurrentGroup(groupId);
			this.doGetListTestSuites(groupId);
		}
	}

	@Override
	protected Widget getViewAsWidget() {
		return (this.view == null) ? null : this.view.asWidget();
	}

	@Override
	public void manageUserProfil(UserProfile userProfile, boolean forceRefresh) {
		super.manageUserProfil(userProfile, forceRefresh);

		if (forceRefresh) {
			this.doGetListTestSuites(this.view.getCurrentGroupId());
		}

	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------- constructor
	public HistoricReportPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IHistoricReportView view) {
		super(service, eventBus);
		this.view = view;

		this.bind();
		this.doInitDatas();
	}

	private void bind() {

		this.view.getGroupHasClickHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				params = null;
				doGetListTestSuites(view.getCurrentGroupId());
			}
		});

		// click handler for view button action
		final ClickHandler viewActionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source != null && source instanceof ButtonViewAction) {
					view.setActionResult(null, null);
					ButtonViewAction button = (ButtonViewAction) source;
					view.selectButton(button);

					ViewActionEnum action = ViewActionEnum.valueOf(button.getViewAction());

					switch (action) {
					case showGroupReport:
						doShowGroupReports();
						break;
					case showAllReport:
						doShowAllReports();
						break;
					case buildUrl:
						doBuildGroupUrl();
						break;

					}
				}
			}
		};
		this.view.setViewActionClickHandler(viewActionClickHandler);

		// click handler for grid action (show, edit, delete)
		final ClickHandler gridActionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = (event == null) ? null : event.getSource();
				if (source != null && source instanceof GridActionButton) {

					GridActionButton button = (GridActionButton) source;
					GridActionButtonEnum action = button.getAction();
					int suiteId = button.getSuiteId();

					switch (action) {

					case delete:
						beforeDeleteSuite(suiteId);
						break;
					case show:
						doShowSuite(suiteId);
						break;
					case edit:
						doEditSuite(suiteId);
						break;
					case url:
						doBuildSuitedUrl(suiteId);
						break;
					}
				}

			}
		};
		this.view.setGridActionClickHandler(gridActionClickHandler);
	}

	// -------------------------------- private methods

	private void doShowAllReports() {
		log.config("doShowAllReports()");
	}

	private void doBuildSuitedUrl(int suiteId) {

		VoTestSuiteForGrid suite = this.mapId2TestSuites.get(suiteId);
		if (suite != null && suite.getUrlToShare() != null) {

			this.doBuildUrl("Public url for report " + suite.getName(), suite.getUrlToShare());
			return;
		}

		this.view.setActionResult("No current report!", LogStatus.warning);
	}

	private void doBuildGroupUrl() {

		if (this.urlToShare != null) {

			int groupId = this.view.getCurrentGroupId();
			VoIdName group = mapId2Groups.get(groupId);

			if (group != null) {
				this.doBuildUrl("Public url for STB " + group.getName(), this.urlToShare);
				return;
			}
		}
		this.view.setActionResult("No current STB!", LogStatus.warning);
	}

	private void doBuildUrl(String title, String url) {
		log.config("doBuildUrl() - urlToShare: " + url);

		String[] messages = new String[4];
		messages[0] = "L'url publique permet d'accéder directement au site html";
		messages[1] = "sans avoir à utiliser cette IHM.";
		messages[2] = "Elle peut être fournie à une équipe ou des acteurs extérieurs";
		messages[3] = "ayant accès au réseau:";

		Label link = new Label(url);
		link.setStyleName(STYLE_LOG_SUCCESS);
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused(title, messages, link, false, null);

		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	private void doEditSuite(int suiteId) {
		log.config("doEditSuite(): " + suiteId);

		ViewReportEvent event = new ViewReportEvent(MainPanelViewEnum.editReport);
		Map<String, Object> params = ObjectUtils.buildMapWithOneItem(PARAMS_SUITE_ID, new Integer(suiteId));
		params.put(PARAMS_GROUP_ID, this.view.getCurrentGroupId());
		event.setParams(params);
		this.eventBus.fireEvent(event);
	}

	private void beforeDeleteSuite(final int suiteId) {
		log.config("beforeDeleteSuite(): " + suiteId);

		VoTestSuiteForGrid suite = this.mapId2TestSuites.get(suiteId);

		String[] message = new String[] { "Confirmer la suppression de la suite de test", suite.getName(),
				"Tous les rapports xml seront également supprimés!" };
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Remove suite from history", message, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						view.setActionResult("Remove suite from history canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						view.setActionResult("Remove suite from history in progres...", LogStatus.warning);
						doDeleteSuite(suiteId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void doDeleteSuite(int suiteId) {

		log.config("doDeleteSuite(): " + suiteId);
		VoTestSuiteForGrid suite = this.mapId2TestSuites.get(suiteId);
		String suiteName = suite.getName();

		final String message = " when deleting suite " + suiteName + "!";

		this.rpcService.deleteTestSuiteFromHistory(suiteId, new MyAsyncCallback<Boolean>("Error" + message) {

			@Override
			public void onSuccess(Boolean result) {

				if (!result) {
					view.setActionResult("Failure" + message, LogStatus.warning);
				} else {
					view.setActionResult("Success" + message, LogStatus.success);
					loadDatas(true);
				}
			}
		});
	}

	private void doShowGroupReports() {
		log.config("doShowGroupReports()");
		view.waiting(true);

		int groupId = this.view.getCurrentGroupId();
		final String message = " when creating html list reports for group " + this.mapId2Groups.get(groupId).getName()
				+ "!";

		this.rpcService.showHtmlListReportForGroup(groupId, new MyAsyncCallback<VoListReportResponse>("Error "
				+ message) {

			@Override
			public void onSuccess(VoListReportResponse response) {

				view.waiting(false);
				if (response == null) {
					view.setActionResult("Failure" + message, LogStatus.warning);
				} else {

					if (response.getUrl() == null) {
						view.setActionResult("Failure" + response.getComment(), LogStatus.warning);
					} else {
						view.setActionResult("Success" + message, LogStatus.success);
						log.config("url: " + response.getUrl());
						Window.open(response.getUrl(), "Single Html report", "");
					}
				}
			}

		});

	}

	private void doShowSuite(int suiteId) {
		log.config("doShowSuite(): " + suiteId);

		view.waiting(true);
		VoTestSuiteForGrid suite = this.mapId2TestSuites.get(suiteId);
		String suiteName = suite.getName();

		final String message = " when creating html single report for suite " + suiteName + "!";

		this.rpcService.showHtmlSingleReport(suiteId, new MyAsyncCallback<VoSingleReportResponse>("Error " + message) {

			@Override
			public void onSuccess(VoSingleReportResponse response) {

				view.waiting(false);
				if (response == null || response.getUrl() == null) {
					view.setActionResult("Failure" + message, LogStatus.warning);
				} else {
					view.setActionResult("Success" + message, LogStatus.success);
					log.config("url: " + response.getUrl());
					Window.open(response.getUrl(), "Single Html report", "");
				}
			}
		});

	}

	/*
	 * Initialisation à la creation du presenter ou lors du changement de vue
	 */
	private void doInitDatas() {

		this.rpcService.getVoInitHistoricReportDatas(new MyAsyncCallback<VoInitHistoricReportDatas>(
				"Error when getting init lists!") {
			@Override
			public void onSuccess(VoInitHistoricReportDatas result) {

				// init des list pour listbox
				view.setInitDatas(result);
				mapId2Groups = VoIdUtils.getMapId2Item(result.getListGroups());
			}
		});
	}

	/*
	 * on cherche en priorite dans les params puis dans la vue
	 */
	private int getCurrentGroupFromParamsOrView() {

		log.config("getCurrentGroupFromParamsOrView()");

		int groupId = IVo.ID_UNDEFINED;

		if (this.params != null) {
			Integer groupFromParams = this.params.containsKey(PARAMS_GROUP_ID) ? (Integer) this.params
					.get(PARAMS_GROUP_ID) : IVo.ID_UNDEFINED;
			groupId = groupFromParams.intValue();

		}

		if (groupId == IVo.ID_UNDEFINED) {
			groupId = this.view.getCurrentGroupId();
		}
		return groupId;
	}

	/*
	 * On rafraichit la liste des suites pour un group
	 */
	private void doGetListTestSuites(final int groupId) {

		if (groupId == IVo.ID_UNDEFINED) {
			view.setDatas(null);
			this.urlToShare = null;
			view.activeButtons(this.no_action);
			return;
		}

		final VoIdName group = this.mapId2Groups.get(groupId);
		final String message = " when getting list of suite for group: " + group.getName();
		this.rpcService.getListTestSuiteByGroup(groupId, new MyAsyncCallback<VoListSuiteForGrid>("Error" + message) {

			@Override
			public void onSuccess(VoListSuiteForGrid response) {

				log.config("getListTestSuiteByGroup() - onSuccess...");
				mapId2TestSuites = VoIdUtils.getMapId2Item(response.getListTestSuites());
				urlToShare = response.getUrlToShare();
				view.setDatas(response);
				view.setActionResult("Success" + message, LogStatus.success);
				view.activeAllButtons();
			}
		});
	}

	// -------------------------------- VIEW
	public interface IHistoricReportView extends IProfilMainView {

		public void setInitDatas(VoInitHistoricReportDatas datas);

		public void setDatas(VoListSuiteForGrid voTestSuites);

		public HasClickHandlers getGroupHasClickHandler();

		public void setGridActionClickHandler(ClickHandler actionClickHandler);

		public int getCurrentGroupId();

		public void setCurrentGroup(int groupId);

	}

	private abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

		private final String errorMessage;

		private MyAsyncCallback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Throwable caught) {

			if (this.errorMessage != null) {
				String errorMessage = this.errorMessage + "<br/>" + caught.getMessage();
				view.setActionResult(errorMessage, LogStatus.error);
				log.severe(errorMessage);
				view.waiting(false);
			}
		}

	}

}
