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
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.client.widget.MyUploader.UploadHandler;
import com.francetelecom.orangetv.junithistory.client.widget.MyUploader.UploadInfo;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitSingleReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportData;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Controller pour la gestion d'un rapport individuel
 * 
 * @author ndmz2720
 * 
 */
public class SingleReportPresenter extends AbstractMainPresenter {

	private final static Logger log = Logger.getLogger("SingleReportPresenter");

	public enum UploadState {
		none(LogStatus.success), started(LogStatus.warning), canceled(LogStatus.warning), error(LogStatus.error), done(
				"ended", LogStatus.success);

		private final LogStatus logStatus;
		private final String text;

		public LogStatus getLogStatus() {
			return this.logStatus;
		}

		public String getText() {
			return (this.text == null) ? this.name() : this.text;
		}

		private UploadState(LogStatus logStatus) {
			this(null, logStatus);
		}

		private UploadState(String text, LogStatus logStatus) {
			this.text = text;
			this.logStatus = logStatus;
		}
	}

	public enum ViewActionEnum {
		showHtmlReport, addToHistory, clearAll;
	}

	private final ISingleReportView view;
	private UploadState uploadState = UploadState.none;
	private String filename;

	private Map<Integer, VoGroupName> mapId2Groups = new HashMap<>();
	private Map<Integer, VoUser> mapId2Users = new HashMap<>();
	private VoSingleReportProtection protection;

	// -------------------------------------- implementing IPresenter

	@Override
	public IMainView getView() {
		return this.view;
	}

	// ------------------------------------- overriding AbstractPresenter

	@Override
	public void manageUserProfil(UserProfile userProfile) {

		super.manageUserProfil(userProfile);

		// on rafraichit la protection en cours >> et l'affichage des bouton
		// d'action
		this.doGetProtection();

	}

	@Override
	protected Widget getViewAsWidget() {
		return (this.view == null) ? null : this.view.asWidget();
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {
		// nothing

	}

	// ------------------------------- constructor
	public SingleReportPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, ISingleReportView view) {
		super(service, eventBus);
		this.view = view;
		this.bind();
		this.doInitView();
	}

	// --------------------------------------- private methods
	private void bind() {

		final ClickHandler buttonClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source != null && source instanceof ButtonViewAction) {
					view.setActionResult(null, null);
					ButtonViewAction button = (ButtonViewAction) source;
					view.selectButton(button);

					ViewActionEnum viewActionEnum = ViewActionEnum.valueOf(button.getViewAction());
					switch (viewActionEnum) {
					case showHtmlReport:
						doShowHtmlReport();
						break;
					case addToHistory:
						beforeAddToHistory();
						break;
					case clearAll:
						doClearAll();
						break;
					}
				}
			}
		};
		this.view.setViewActionClickHandler(buttonClickHandler);

		this.view.setUploadHandler(new UploadHandler() {

			int count = 0;

			@Override
			public void onOpenDialog() {
				setUploadState(UploadState.none);
				view.setActionResult(null, null);
				view.activeButtons(ViewActionEnum.clearAll.name());
			}

			@Override
			public void beforeStarting(int countOfFile) {
				count = countOfFile;
				doBeforeStartNewUpload();
			}

			@Override
			public void onQueue(String name) {
				setUploadState(UploadState.started);
				filename = name;
				view.setActionResult("uploading " + filename, LogStatus.warning);
			}

			@Override
			public void onCancel(String name) {
				setUploadState(UploadState.canceled);
				view.setActionResult("upload canceled: " + name, LogStatus.warning);

			}

			@Override
			public void onError(String name, String errorMessage) {
				setUploadState(UploadState.error);
				filename = name;
				view.setActionResult(errorMessage, LogStatus.error);

			}

			@Override
			public void onFinish(UploadInfo uploadInfo) {
				count--;
				log.config("count: " + count);
				view.setActionResult("upload ended: " + uploadInfo.getFilename(), LogStatus.warning);
				if (count == 0) {
					setUploadState(UploadState.done);
					view.setActionResult("uploading done!", LogStatus.success);
					doAfterUploadEnded();
				}

			}

		});
	}

	private void setUploadState(UploadState state) {
		this.uploadState = state;
		this.manageButtons();
	}

	private void doInitView() {

		log.config("doInitView()");
		this.rpcService.getVoInitSingleReportDatas(new MyAsyncCallback<VoInitSingleReportDatas>(
				"Error when getting init lists!") {
			@Override
			public void onSuccess(VoInitSingleReportDatas result) {
				mapId2Groups = VoIdUtils.getMapId2Item(result.getListGroups());
				mapId2Users = VoIdUtils.getMapId2Item(result.getListUsers());
				view.setInitDatas(result);
			}
		});
	}

	private void doGetProtection() {

		log.config("doGetProtection()");
		this.rpcService.getSingleReportProtection(new MyAsyncCallback<VoSingleReportProtection>("") {

			@Override
			public void onSuccess(VoSingleReportProtection result) {
				manageProtection(result);
			}

		});
	}

	// modification de la protection suite changement de profile ou à
	// l'initialisation
	private void manageProtection(VoSingleReportProtection protection) {

		this.protection = protection;
		if (this.protection != null && !this.protection.canAddToHistory()) {
			log.warning("AddToHistory protected!");
		}
		this.manageButtons();

	}

	// activation des boutons en fonction de l'état de upload et de la
	// protection
	private void manageButtons() {

		switch (this.uploadState) {

		case canceled:
		case error:
		case none:
		case started:
			view.activeButtons(ViewActionEnum.clearAll.name());
			break;

		case done:
			if (this.canAddToHistory()) {
				view.activeButtons(ViewActionEnum.showHtmlReport.name(), ViewActionEnum.addToHistory.name(),
						ViewActionEnum.clearAll.name());
			} else {
				view.activeButtons(ViewActionEnum.showHtmlReport.name(), ViewActionEnum.clearAll.name());
			}
			break;

		}

	}

	private boolean canAddToHistory() {

		return this.protection == null ? false : this.protection.canAddToHistory();
	}

	private void doShowHtmlReport() {
		final VoSingleReportData datas = this.view.getSingleReportData();
		this.rpcService.showHtmlSingleReport(datas, new MyAsyncCallback<VoSingleReportResponse>(
				"Error when creating html single report!") {

			@Override
			public void onSuccess(VoSingleReportResponse response) {

				view.setDatas(response.getGroupId(), response.getVersion());
				if (response == null || response.getUrl() == null) {
					view.setActionResult("Failure when creating html single report!", LogStatus.warning);
				} else {
					view.setActionResult("Success when creating html single report!", LogStatus.success);
					log.config("url: " + response.getUrl());
					Window.open(response.getUrl(), "Single Html report", "");
				}
			}
		});
	}

	private String[] getUploadDescription() {

		VoSingleReportData datas = view.getSingleReportData();

		final VoGroupName group = this.mapId2Groups.get(datas.getGroupId());
		final VoUser user = this.mapId2Users.get(datas.getUserId());

		String[] description = new String[5];
		description[0] = "STB     : " + ((group == null) ? "undefined" : group.getName());
		description[1] = "FIRMWARE: " + ((datas.getFirmware() == null) ? "undefined" : datas.getFirmware());
		description[2] = "IPTVKIT : " + ((datas.getIptvkit() == null) ? "undefined" : datas.getIptvkit());
		description[3] = "USER    : " + ((user == null) ? "undefined" : user.getName());
		description[4] = "DATE    : " + ((datas.getDate() == null) ? "undefined" : DF.format(datas.getDate()));
		return description;
	}

	void beforeAddToHistory() {

		String[] description = this.getUploadDescription();
		int size = 2 + description.length;
		String[] arguments = new String[size];
		arguments[0] = "Confirmer l'ajout du rapport ";
		for (int i = 0; i < description.length; i++) {
			String line = description[i];
			arguments[i + 1] = line;
		}
		arguments[size - 1] = " à l'historique global ?";

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Add report to history", arguments, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						view.setActionResult("Add to history canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						view.setActionResult("Add to history in progres...", LogStatus.warning);
						doValidAddToHistory();
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void doValidAddToHistory() {

		view.waiting(true);
		final VoSingleReportData datas = this.view.getSingleReportData();
		this.rpcService.validSingleReport(datas, new MyAsyncCallback<VoDatasValidation>(
				"Error when adding single report to history!") {

			@Override
			public void onSuccess(VoDatasValidation validation) {

				view.waiting(false);

				if (!validation.isValid()) {
					view.setActionResult("Failure when validating single report!", LogStatus.warning);

					String[] messages = ObjectUtils.listToTab(validation.getErrorMessages());
					DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Datas error", messages, null, false,
							null);
					WidgetUtils.centerDialogAndShow(dialogBox);
				} else {
					doAddToHistory();
				}
			}
		});

	}

	private void doAddToHistory() {
		view.waiting(true);
		final VoSingleReportData datas = this.view.getSingleReportData();
		this.rpcService.addSingleReportToHistory(datas, new MyAsyncCallback<VoSingleReportResponse>(
				"Error when adding single report to history!") {

			@Override
			public void onSuccess(VoSingleReportResponse response) {

				view.waiting(false);
				view.setDatas(response.getGroupId(), null);
				view.setActionResult("Success when adding single report to history!", LogStatus.success);

				afterAddToHistory("Suite " + response.getSuiteName() + " added to history!", response.getGroupId());

			}
		});
	}

	private void afterAddToHistory(String message, int groupId) {

		log.config("afterAddToHistory() - groupId: " + groupId);
		final DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Added to history",
				new String[] { message }, null, false, null);
		WidgetUtils.centerDialogAndShow(dialogBox);

		final ViewReportEvent event = new ViewReportEvent(MainPanelViewEnum.historicReport);
		final Map<String, Object> params = new HashMap<>(0);
		params.put(PARAMS_GROUP_ID, new Integer(groupId));
		event.setParams(params);
		eventBus.fireEvent(event);
	}

	private void doBeforeStartNewUpload() {
		this.rpcService.beforeStartNewUpload(new MyAsyncCallback<Void>(null) {

			@Override
			public void onSuccess(Void result) {
				view.waiting(true);
				view.setDatas(IVo.ID_UNDEFINED, "");
			}
		});
	}

	private void doAfterUploadEnded() {
		this.rpcService.afterUploadEnd(new MyAsyncCallback<Void>(null) {
			@Override
			public void onSuccess(Void result) {
				view.waiting(false);
				log.config("doAfterUploadEnded()");
				manageButtons();
			}
		});
	}

	private void doClearAll() {
		this.view.reinit();
		this.view.selectButton(null);
	}

	// -------------------------------- VIEW
	public interface ISingleReportView extends IMainView {

		public void setDatas(int groupId, String version);

		public void setInitDatas(VoInitSingleReportDatas datas);

		public VoSingleReportData getSingleReportData();

		public void setUploadHandler(UploadHandler handler);

	}

	public static class UploadResult {

		private final String id;
		private final UploadState uploadState;

		public UploadResult(String id, UploadState uploaState) {
			this.id = id;
			this.uploadState = uploaState;
		}

		public String getId() {
			return id;
		}

		public UploadState getUploadState() {
			return uploadState;
		}
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
